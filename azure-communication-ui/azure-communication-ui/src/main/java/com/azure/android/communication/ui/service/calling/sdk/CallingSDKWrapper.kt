// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service.calling.sdk

import android.content.Context
import com.azure.android.communication.calling.AudioOptions
import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.DeviceManager
import com.azure.android.communication.calling.DiagnosticOptions
import com.azure.android.communication.calling.GroupCallLocator
import com.azure.android.communication.calling.HangUpOptions
import com.azure.android.communication.calling.JoinCallOptions
import com.azure.android.communication.calling.JoinMeetingLocator
import com.azure.android.communication.calling.LocalVideoStream
import com.azure.android.communication.calling.TeamsMeetingLinkLocator
import com.azure.android.communication.calling.VideoDevicesUpdatedListener
import com.azure.android.communication.calling.VideoOptions
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.CallConfiguration
import com.azure.android.communication.ui.configuration.CallType
import com.azure.android.communication.ui.logger.Logger
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow

internal class CallingSDKWrapper(
    private val instanceId: Int,
    private val context: Context,
    private val callingSDKEventHandler: CallingSDKEventHandler,
    private val logger: Logger? = null,
) {
    private var nullableCall: Call? = null
    private var callClient: CallClient? = null

    private var callAgentCompletableFuture: CompletableFuture<CallAgent>? = null
    private var deviceManagerCompletableFuture: CompletableFuture<DeviceManager>? = null
    private var localVideoStreamCompletableFuture: CompletableFuture<LocalVideoStream>? = null
    private var endCallCompletableFuture: CompletableFuture<Void>? = null
    private var camerasInitializedCompletableFuture: CompletableFuture<Void>? = null

    private val configuration get() = CallCompositeConfiguration.getConfig(instanceId)
    private var videoDevicesUpdatedListener: VideoDevicesUpdatedListener? = null

    private val callConfig: CallConfiguration
        get() {
            if (configuration.callConfig == null)
                throw IllegalStateException("Call configurations are not set")

            return configuration.callConfig!!
        }

    private val call: Call
        get() {
            if (nullableCall == null)
                throw IllegalStateException("Call is not started")

            return nullableCall!!
        }

    fun getRemoteParticipantsMap() = callingSDKEventHandler.getRemoteParticipantsMap()

    fun getCallingStateWrapperSharedFlow() =
        callingSDKEventHandler.getCallingStateWrapperSharedFlow()

    fun getIsMutedSharedFlow() = callingSDKEventHandler.getIsMutedSharedFlow()

    fun getIsRecordingSharedFlow() = callingSDKEventHandler.getIsRecordingSharedFlow()

    fun getIsTranscribingSharedFlow() = callingSDKEventHandler.getIsTranscribingSharedFlow()

    fun getRemoteParticipantInfoModelSharedFlow(): Flow<Map<String, ParticipantInfoModel>> =
        callingSDKEventHandler.getRemoteParticipantInfoModelFlow()

    fun endCall(): CompletableFuture<Void> {
        val call: Call?

        try {
            call = this.call
        } catch (e: Exception) {
            // We can't access the call currently, return a no-op and exit
            return CompletableFuture.runAsync { }
        }

        callingSDKEventHandler.onEndCall()
        endCallCompletableFuture = call.hangUp(HangUpOptions())
        return endCallCompletableFuture!!
    }

    fun dispose() {
        callingSDKEventHandler.dispose()
        cleanupResources()
    }

    fun setupCall() {
        if (callClient == null) {
            callClient = getCallClient()
        }
        createDeviceManager()
    }

    fun startCall(cameraState: CameraState, audioState: AudioState): CompletableFuture<Void> {
        val startCallCompletableFuture = CompletableFuture<Void>()
        createCallAgent().thenAccept { agent: CallAgent ->
            val audioOptions = AudioOptions()
            audioOptions.isMuted = (audioState.operation != AudioOperationalStatus.ON)

            var videoOptions: VideoOptions? = null
            if (cameraState.operation == CameraOperationalStatus.ON) {
                val localVideoStreams = arrayOf(getLocalVideoStream().get())
                videoOptions = VideoOptions(localVideoStreams)
            }

            val callLocator: JoinMeetingLocator = when (callConfig.callType) {
                CallType.GROUP_CALL -> GroupCallLocator(callConfig.groupId)
                CallType.TEAMS_MEETING -> TeamsMeetingLinkLocator(callConfig.meetingLink)
            }
            joinCall(agent, audioOptions, videoOptions, callLocator)

            startCallCompletableFuture.complete(null)
        }
            .exceptionally { error ->
                startCallCompletableFuture.completeExceptionally(error)
                null
            }

        return startCallCompletableFuture
    }

    fun turnOnVideoAsync(): CompletableFuture<LocalVideoStream> {
        val result = CompletableFuture<LocalVideoStream>()
        this.getLocalVideoStream()
            .thenCompose { videoStream: LocalVideoStream ->
                call.startVideo(context, videoStream).whenComplete { _, error: Throwable? ->
                    if (error != null) {
                        result.completeExceptionally(error)
                    } else {
                        result.complete(videoStream)
                    }
                }
            }
            .exceptionally { error ->
                result.completeExceptionally(error)
                null
            }

        return result
    }

    fun turnOffVideoAsync(): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()
        this.getLocalVideoStream()
            .thenAccept { videoStream: LocalVideoStream ->
                call.stopVideo(context, videoStream)
                    .whenComplete { _, error: Throwable? ->
                        if (error != null) {
                            result.completeExceptionally(error)
                        } else {
                            result.complete(null)
                        }
                    }
            }
            .exceptionally { error ->
                result.completeExceptionally(error)
                null
            }
        return result
    }

    fun switchCameraAsync(): CompletableFuture<CameraDeviceSelectionStatus> {
        val result = CompletableFuture<CameraDeviceSelectionStatus>()
        this.getLocalVideoStream()
            .thenAccept { videoStream: LocalVideoStream ->
                val desiredCameraState = when (videoStream.source.cameraFacing) {
                    CameraFacing.FRONT -> CameraFacing.BACK
                    else -> CameraFacing.FRONT
                }

                initializeCameras().thenAccept {

                    val desiredCamera =
                        getCamera(
                            desiredCameraState,
                        )

                    if (desiredCamera == null) {
                        result.completeExceptionally(null)
                    } else {
                        videoStream.switchSource(desiredCamera)
                            .exceptionally {
                                result.completeExceptionally(it)
                                null
                            }.thenRun {
                                val cameraDeviceSelectionStatus =
                                    when (desiredCamera.cameraFacing) {
                                        CameraFacing.FRONT -> CameraDeviceSelectionStatus.FRONT
                                        CameraFacing.BACK -> CameraDeviceSelectionStatus.BACK
                                        else -> null
                                    }

                                when (cameraDeviceSelectionStatus) {
                                    null -> result.completeExceptionally(
                                        Throwable(
                                            "Not supported camera facing type"
                                        )
                                    )
                                    else -> result.complete(cameraDeviceSelectionStatus)
                                }
                            }
                    }
                }
            }
            .exceptionally { error ->
                result.completeExceptionally(error)
                null
            }

        return result
    }

    fun turnOnMicAsync(): CompletableFuture<Void> {
        return call.unmute(context)
    }

    fun turnOffMicAsync(): CompletableFuture<Void> {
        return call.mute(context)
    }

    fun getLocalVideoStream(): CompletableFuture<LocalVideoStream> {
        val result = CompletableFuture<LocalVideoStream>()

        val localVideoStreamCompletableFuture = getLocalVideoStreamCompletableFuture()

        if (localVideoStreamCompletableFuture.isDone) {
            result.complete(localVideoStreamCompletableFuture.get())
        } else {
            initializeCameras().whenComplete { _, error ->
                if (error != null) {
                    localVideoStreamCompletableFuture.completeExceptionally(error)
                    result.completeExceptionally(error)
                } else {
                    val desiredCamera = getCamera(CameraFacing.FRONT)

                    localVideoStreamCompletableFuture.complete(
                        LocalVideoStream(
                            desiredCamera,
                            context
                        )
                    )
                    result.complete(localVideoStreamCompletableFuture.get())
                }
            }
        }

        return result
    }

    private fun createCallAgent(): CompletableFuture<CallAgent> {

        if (callAgentCompletableFuture == null || callAgentCompletableFuture!!.isCompletedExceptionally) {
            callAgentCompletableFuture = CompletableFuture<CallAgent>()
            val options = CallAgentOptions().apply { displayName = callConfig.displayName }
            try {
                val createCallAgentFutureCompletableFuture = callClient!!.createCallAgent(
                    context,
                    callConfig.communicationTokenCredential,
                    options
                )
                createCallAgentFutureCompletableFuture.whenComplete { callAgent: CallAgent, error: Throwable? ->
                    if (error != null) {
                        callAgentCompletableFuture!!.completeExceptionally(error)
                    } else {
                        callAgentCompletableFuture!!.complete(callAgent)
                    }
                }
            } catch (error: Throwable) {
                callAgentCompletableFuture!!.completeExceptionally(error)
            }
        }

        return callAgentCompletableFuture!!
    }

    private fun joinCall(
        agent: CallAgent,
        audioOptions: AudioOptions,
        videoOptions: VideoOptions?,
        joinMeetingLocator: JoinMeetingLocator,
    ) {
        val joinCallOptions = JoinCallOptions()
        joinCallOptions.audioOptions = audioOptions
        videoOptions?.let { joinCallOptions.videoOptions = videoOptions }

        nullableCall = agent.join(context, joinMeetingLocator, joinCallOptions)
        callingSDKEventHandler.onJoinCall(call)
    }

    private fun getDeviceManagerCompletableFuture(): CompletableFuture<DeviceManager> {
        if (deviceManagerCompletableFuture == null ||
            deviceManagerCompletableFuture?.isCompletedExceptionally == true
        ) {
            deviceManagerCompletableFuture = CompletableFuture<DeviceManager>()
        }
        return deviceManagerCompletableFuture!!
    }

    private fun createDeviceManager() {
        val deviceManagerCompletableFuture = getDeviceManagerCompletableFuture()

        if (deviceManagerCompletableFuture.isCompletedExceptionally ||
            !deviceManagerCompletableFuture.isDone
        ) {
            callClient!!.getDeviceManager(context)
                .whenComplete { deviceManager: DeviceManager, getDeviceManagerError ->
                    if (getDeviceManagerError != null) {
                        deviceManagerCompletableFuture.completeExceptionally(
                            getDeviceManagerError
                        )
                    } else {
                        deviceManagerCompletableFuture.complete(deviceManager)
                    }
                }
        }

        CompletableFuture.allOf(
            deviceManagerCompletableFuture,
        )
    }

    private fun initializeCameras(): CompletableFuture<Void> {
        if (camerasInitializedCompletableFuture == null) {
            camerasInitializedCompletableFuture = CompletableFuture<Void>()
            getDeviceManagerCompletableFuture().whenComplete { deviceManager: DeviceManager, _: Throwable? ->
                completeCamerasInitializedCompletableFuture()
                videoDevicesUpdatedListener =
                    VideoDevicesUpdatedListener {
                        completeCamerasInitializedCompletableFuture()
                    }
                deviceManager.addOnCamerasUpdatedListener(videoDevicesUpdatedListener)
            }
        }

        return camerasInitializedCompletableFuture!!
    }

    private fun completeCamerasInitializedCompletableFuture() {
        if (doFrontAndBackCamerasExist()) {
            camerasInitializedCompletableFuture?.complete(null)
        }
    }

    private fun doFrontAndBackCamerasExist(): Boolean {
        return getCamera(CameraFacing.FRONT) != null &&
            getCamera(CameraFacing.BACK) != null
    }

    private fun getCamera(
        cameraFacing: CameraFacing,
    ) = getDeviceManagerCompletableFuture().get().cameras?.find {
        it.cameraFacing.name.equals(
            cameraFacing.name,
            ignoreCase = true
        )
    }

    private fun getLocalVideoStreamCompletableFuture(): CompletableFuture<LocalVideoStream> {
        if (localVideoStreamCompletableFuture == null || localVideoStreamCompletableFuture?.isCompletedExceptionally == true ||
            localVideoStreamCompletableFuture?.isCancelled == true
        ) {
            localVideoStreamCompletableFuture = CompletableFuture<LocalVideoStream>()
        }
        return localVideoStreamCompletableFuture!!
    }

    private fun cleanupResources() {
        videoDevicesUpdatedListener?.let {
            deviceManagerCompletableFuture?.get()?.removeOnCamerasUpdatedListener(it)
        }
        callAgentCompletableFuture?.get()?.dispose()
        callClient = null
        nullableCall = null
        callAgentCompletableFuture = null
        localVideoStreamCompletableFuture = null
        camerasInitializedCompletableFuture = null
        deviceManagerCompletableFuture = null
        endCallCompletableFuture?.complete(null)
    }

    private fun getCallClient(): CallClient {
        val callClientOptions = CallClientOptions().apply {
            configuration.callConfig?.diagnosticConfig?.tags?.let { telemetryTags ->
                val diagnosticOptions = getOrCreateDiagnosticOptions(this)
                diagnostics.tags = diagnosticOptions.tags + telemetryTags
            }
        }
        logger?.let { logger ->
            callClientOptions.diagnostics.tags.forEach {
                logger.debug("diagnostic tag element: $it")
            }
        }
        return CallClient(callClientOptions)
    }

    private fun getOrCreateDiagnosticOptions(callClientOptions: CallClientOptions): DiagnosticOptions {
        if (callClientOptions.diagnostics == null) {
            callClientOptions.diagnostics = DiagnosticOptions()
        }

        return callClientOptions.diagnostics
    }
}
