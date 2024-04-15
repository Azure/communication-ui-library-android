// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.content.Context
import com.azure.android.communication.calling.AcceptCallOptions
import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.DeviceManager
import com.azure.android.communication.calling.GroupCallLocator
import com.azure.android.communication.calling.HangUpOptions
import com.azure.android.communication.calling.JoinCallOptions
import com.azure.android.communication.calling.JoinMeetingLocator
import com.azure.android.communication.calling.OutgoingAudioOptions
import com.azure.android.communication.calling.OutgoingVideoOptions
import com.azure.android.communication.calling.RoomCallLocator
import com.azure.android.communication.calling.StartCallOptions
import com.azure.android.communication.calling.TeamsMeetingLinkLocator
import com.azure.android.communication.calling.VideoDevicesUpdatedListener
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.configuration.CallConfiguration
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomOptions
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.azure.android.communication.calling.LocalVideoStream as NativeLocalVideoStream

internal class CallingSDKWrapper(
    private val context: Context,
    private val callingSDKEventHandler: CallingSDKEventHandler,
    private val callConfigInjected: CallConfiguration?,
    private val callingSDKCallAgentWrapper: CallingSDKCallAgentWrapper,
    private val telecomOptions: CallCompositeTelecomOptions?,
) : CallingSDK {
    private var setupCallCompletableFuture: CompletableFuture<Void> = CompletableFuture()

    private var deviceManagerCompletableFuture: CompletableFuture<DeviceManager>? = null
    private var localVideoStreamCompletableFuture: CompletableFuture<LocalVideoStream>? = null
    private var endCallCompletableFuture: CompletableFuture<Void>? = null
    private var camerasInitializedCompletableFuture: CompletableFuture<Void>? = null

    private var videoDevicesUpdatedListener: VideoDevicesUpdatedListener? = null
    private var camerasCountStateFlow = MutableStateFlow(0)

    private var nullableCall: Call? = null
    private var callClientInternal: CallClient? = null
    private val incomingCallWrapper: IncomingCallWrapper? = callingSDKCallAgentWrapper.incomingCallWrapper

    private val callConfig: CallConfiguration
        get() {
            try {
                return callConfigInjected!!
            } catch (ex: Exception) {
                throw CallCompositeException(
                    "Call configurations are not set",
                    IllegalStateException()
                )
            }
        }

    val call: Call
        get() {
            try {
                return nullableCall!!
            } catch (ex: Exception) {
                throw CallCompositeException("Call is not started", IllegalStateException())
            }
        }

    private val callClient: CallClient
        get() {
            try {
                return callClientInternal!!
            } catch (ex: Exception) {
                throw CallCompositeException("Call is not started", IllegalStateException())
            }
        }

    override fun getRemoteParticipantsMap(): Map<String, RemoteParticipant> =
        callingSDKEventHandler.getRemoteParticipantsMap().mapValues { it.value.into() }

    override fun getCamerasCountStateFlow() = camerasCountStateFlow

    override fun getCallingStateWrapperSharedFlow() =
        callingSDKEventHandler.getCallingStateWrapperSharedFlow()

    override fun getCallIdStateFlow(): StateFlow<String?> = callingSDKEventHandler.getCallIdStateFlow()

    override fun getLocalParticipantRoleSharedFlow() =
        callingSDKEventHandler.getCallParticipantRoleSharedFlow()

    override fun getIsMutedSharedFlow() = callingSDKEventHandler.getIsMutedSharedFlow()

    override fun getIsRecordingSharedFlow() = callingSDKEventHandler.getIsRecordingSharedFlow()

    override fun getIsTranscribingSharedFlow() =
        callingSDKEventHandler.getIsTranscribingSharedFlow()

    //region Call Diagnostics
    override fun getNetworkQualityCallDiagnosticSharedFlow() =
        callingSDKEventHandler.getNetworkQualityCallDiagnosticsSharedFlow()

    override fun getNetworkCallDiagnosticSharedFlow() =
        callingSDKEventHandler.getNetworkCallDiagnosticsSharedFlow()

    override fun getMediaCallDiagnosticSharedFlow() =
        callingSDKEventHandler.getMediaCallDiagnosticsSharedFlow()
    //endregion
    override fun getDominantSpeakersSharedFlow() =
        callingSDKEventHandler.getDominantSpeakersSharedFlow()

    override fun getRemoteParticipantInfoModelSharedFlow(): Flow<Map<String, ParticipantInfoModel>> =
        callingSDKEventHandler.getRemoteParticipantInfoModelFlow()

    override fun hold(): CompletableFuture<Void> {
        val call: Call?

        try {
            call = this.call
        } catch (e: Exception) {
            // We can't access the call currently, return a no-op and exit
            return CompletableFuture.runAsync { }
        }

        return call.hold()
    }

    override fun resume(): CompletableFuture<Void> {
        val call: Call?

        try {
            call = this.call
        } catch (e: Exception) {
            // We can't access the call currently, return a no-op and exit
            return CompletableFuture.runAsync { }
        }

        return call.resume()
    }

    override fun startIncomingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.startAudio(context, call.activeIncomingAudioStream).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun startOutgoingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.startAudio(context, call.activeOutgoingAudioStream).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun unmuteIncomingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.unmuteIncomingAudio(context).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun unmuteOutgoingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.unmuteOutgoingAudio(context).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun stopIncomingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.stopAudio(context, call.activeIncomingAudioStream).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun stopOutgoingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.stopAudio(context, call.activeOutgoingAudioStream).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun muteIncomingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.muteIncomingAudio(context).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun muteOutgoingAudio(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.muteOutgoingAudio(context).whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun holdCall(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.hold().whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun resumeCall(): java.util.concurrent.CompletableFuture<Void> {
        val future = java.util.concurrent.CompletableFuture<Void>()
        call.resume().whenComplete { result, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(result)
            }
        }
        return future
    }

    override fun endCall(): CompletableFuture<Void> {
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

    override fun admitAll(): CompletableFuture<CallCompositeLobbyErrorCode?> {
        val future = CompletableFuture<CallCompositeLobbyErrorCode?>()
        if (lobbyNullCheck(future)) return future
        nullableCall?.callLobby?.admitAll()?.whenComplete { _, error ->
            if (error != null) {
                var errorCode = CallCompositeLobbyErrorCode.UNKNOWN_ERROR
                if (error.cause is CallingCommunicationException) {
                    errorCode = getLobbyErrorCode(error.cause as CallingCommunicationException)
                }
                future.complete(errorCode)
            } else {
                future.complete(null)
            }
        }
        return future
    }

    override fun admit(userIdentifier: String): CompletableFuture<CallCompositeLobbyErrorCode?> {
        val future = CompletableFuture<CallCompositeLobbyErrorCode?>()
        if (lobbyNullCheck(future)) return future
        val participant = nullableCall?.remoteParticipants?.find { it.identifier.rawId.equals(userIdentifier) }
        participant?.let {
            nullableCall?.callLobby?.admit(listOf(it.identifier))?.whenComplete { _, error ->
                if (error != null) {
                    var errorCode = CallCompositeLobbyErrorCode.UNKNOWN_ERROR
                    if (error.cause is CallingCommunicationException) {
                        errorCode = getLobbyErrorCode(error.cause as CallingCommunicationException)
                    }
                    future.complete(errorCode)
                } else {
                    future.complete(null)
                }
            }
        }
        return future
    }

    private fun lobbyNullCheck(future: CompletableFuture<CallCompositeLobbyErrorCode?>): Boolean {
        if (nullableCall == null || nullableCall?.callLobby == null) {
            future.complete(CallCompositeLobbyErrorCode.UNKNOWN_ERROR)
            return true
        }
        return false
    }

    override fun decline(userIdentifier: String): CompletableFuture<CallCompositeLobbyErrorCode?> {
        val future = CompletableFuture<CallCompositeLobbyErrorCode?>()
        if (lobbyNullCheck(future)) return future
        val participant = nullableCall?.remoteParticipants?.find { it.identifier.rawId.equals(userIdentifier) }
        participant?.let {
            nullableCall?.callLobby?.reject(it.identifier)
                ?.whenComplete { _, error ->
                    if (error != null) {
                        var errorCode = CallCompositeLobbyErrorCode.UNKNOWN_ERROR
                        if (error.cause is CallingCommunicationException) {
                            errorCode = getLobbyErrorCode(error.cause as CallingCommunicationException)
                        }
                        future.complete(errorCode)
                    } else {
                        future.complete(null)
                    }
                }
        }
        return future
    }

    override fun dispose() {
        callingSDKCallAgentWrapper.incomingCallWrapper?.dispose()
        callingSDKEventHandler.dispose()
        cleanupResources()
    }

    override fun setupCall(): CompletableFuture<Void> {
        callingSDKCallAgentWrapper.setupCall()?.whenComplete { callClient, _ ->
            callClientInternal = callClient
            createDeviceManager().handle { _, error: Throwable? ->
                if (error != null) {
                    setupCallCompletableFuture.completeExceptionally(error)
                } else {
                    setupCallCompletableFuture.complete(null)
                }
            }
        }
        return setupCallCompletableFuture
    }

    override fun startCall(
        cameraState: CameraState,
        audioState: AudioState,
    ): CompletableFuture<Void> {

        val startCallCompletableFuture = CompletableFuture<Void>()
        callingSDKCallAgentWrapper.createCallAgent(
            context = context,
            name = callConfig.displayName,
            communicationTokenCredential = callConfig.communicationTokenCredential,
            callConfig.disableInternalPushForIncomingCall,
            telecomOptions,
        ).thenAccept { agent: CallAgent ->
            val audioOptions = OutgoingAudioOptions()
            audioOptions.isMuted = (audioState.operation != AudioOperationalStatus.ON)
            audioOptions.isCommunicationAudioModeEnabled = true
            val callLocator: JoinMeetingLocator? = when (callConfig.callType) {
                CallType.GROUP_CALL -> GroupCallLocator(callConfig.groupId)
                CallType.TEAMS_MEETING -> TeamsMeetingLinkLocator(callConfig.meetingLink)
                CallType.ROOMS_CALL -> RoomCallLocator(callConfig.roomId)
                else -> {
                    null
                }
            }
            // it is possible to have camera state not on, (Example: waiting for local video stream)
            // if camera on is in progress, the waiting will make sure for starting call with right state
            if (camerasCountStateFlow.value != 0 && cameraState.operation != CameraOperationalStatus.OFF) {
                getLocalVideoStream().whenComplete { videoStream, error ->
                    val videoOptions = OutgoingVideoOptions()
                    if (error == null) {
                        val localVideoStreams =
                            arrayOf(videoStream.native as NativeLocalVideoStream)
                        videoOptions.setOutgoingVideoStreams(localVideoStreams.asList())
                    }
                    when (callConfig.callType) {
                        CallType.ONE_TO_N_CALL_INCOMING -> {
                            incomingCallWrapper?.incomingCall()?.let {
                                val acceptCallOptions = AcceptCallOptions()
                                videoOptions.let { acceptCallOptions.outgoingVideoOptions = videoOptions }
                                acceptCallOptions.outgoingAudioOptions = audioOptions
                                nullableCall = it.accept(context, acceptCallOptions)?.get()
                                callingSDKEventHandler.onJoinCall(call)
                                return@whenComplete
                            }
                        }
                        CallType.ONE_TO_N_CALL_OUTGOING -> {
                            callConfig.participants?.let {
                                startCall(agent, audioOptions, videoOptions, it)
                            }
                        }
                        else -> {
                            callLocator?.let {
                                joinCall(agent, audioOptions, videoOptions, callLocator)
                                return@whenComplete
                            }
                        }
                    }
                }.exceptionally { error ->
                    onJoinCallFailed(startCallCompletableFuture, error)
                }
            } else {
                when (callConfig.callType) {
                    CallType.ONE_TO_N_CALL_INCOMING -> {
                        incomingCallWrapper?.incomingCall()?.let {
                            val acceptCallOptions = AcceptCallOptions()
                            acceptCallOptions.outgoingVideoOptions = null
                            acceptCallOptions.outgoingAudioOptions = audioOptions
                            nullableCall = it.accept(context, acceptCallOptions)?.get()
                            callingSDKEventHandler.onJoinCall(call)
                            return@thenAccept
                        }
                    }
                    CallType.ONE_TO_N_CALL_OUTGOING -> {
                        callConfig.participants?.let {
                            startCall(agent, audioOptions, null, it)
                        }
                    }
                    else -> {
                        callLocator?.let {
                            joinCall(agent, audioOptions, null, callLocator)
                            return@thenAccept
                        }
                    }
                }
            }

            startCallCompletableFuture.complete(null)
        }
            .exceptionally { error ->
                onJoinCallFailed(startCallCompletableFuture, error)
            }

        return startCallCompletableFuture
    }

    override fun turnOnVideoAsync(): CompletableFuture<LocalVideoStream> {
        val result = CompletableFuture<LocalVideoStream>()
        this.getLocalVideoStream()
            .thenCompose { videoStream: LocalVideoStream ->
                call.startVideo(context, videoStream.native as NativeLocalVideoStream)
                    .whenComplete { _, error: Throwable? ->
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

    override fun turnOffVideoAsync(): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()
        this.getLocalVideoStream()
            .thenAccept { videoStream: LocalVideoStream ->
                call.stopVideo(context, videoStream.native as NativeLocalVideoStream)
                    .whenComplete { _, error: Throwable? ->
                        if (error != null) {
                            result.completeExceptionally(error)
                        } else {
                            result.complete(null)
                        }
                    }
            }
            .exceptionally { error ->
                onJoinCallFailed(result, error)
            }
        return result
    }

    override fun switchCameraAsync(): CompletableFuture<CameraDeviceSelectionStatus> {
        return if (isAndroidTV(context)) {
            switchCameraAsyncAndroidTV()
        } else {
            switchCameraAsyncMobile()
        }
    }

    override fun turnOnMicAsync(): CompletableFuture<Void> {
        return call.unmuteOutgoingAudio(context)
    }

    override fun turnOffMicAsync(): CompletableFuture<Void> {
        return call.muteOutgoingAudio(context)
    }

    override fun getLocalVideoStream(): CompletableFuture<LocalVideoStream> {
        val result = CompletableFuture<LocalVideoStream>()
        setupCallCompletableFuture.whenComplete { _, error ->
            if (error == null) {
                val localVideoStreamCompletableFuture = getLocalVideoStreamCompletableFuture()

                if (localVideoStreamCompletableFuture.isDone) {
                    result.complete(localVideoStreamCompletableFuture.get())
                } else if (!canCreateLocalVideoStream()) {
                    // cleanUpResources() could have been called before this, so we need to check if it's still
                    // alright to call initializeCameras()
                    result.complete(null)
                } else {
                    initializeCameras().whenComplete { _, error ->
                        if (error != null) {
                            localVideoStreamCompletableFuture.completeExceptionally(error)
                            result.completeExceptionally(error)
                        } else {
                            val desiredCamera = if (isAndroidTV(context)) {
                                getCameraByFacingTypeSelection()
                            } else {
                                getCamera(CameraFacing.FRONT)
                            }

                            localVideoStreamCompletableFuture.complete(
                                LocalVideoStreamWrapper(
                                    NativeLocalVideoStream(
                                        desiredCamera,
                                        context
                                    )
                                )
                            )
                            result.complete(localVideoStreamCompletableFuture.get())
                        }
                    }
                }
            }
        }

        return result
    }

    override fun registerPushNotification(deviceRegistrationToken: String): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()
        setupCall().whenComplete { _, setUpError ->
            if (setUpError != null) {
                result.completeExceptionally(setUpError)
                return@whenComplete
            }

            callingSDKCallAgentWrapper.createCallAgent(
                context = context,
                name = callConfig.displayName,
                communicationTokenCredential = callConfig.communicationTokenCredential,
                callConfig.disableInternalPushForIncomingCall,
                telecomOptions,
            ).thenAccept { agent: CallAgent ->
                agent.registerPushNotification(deviceRegistrationToken)
                    .whenComplete { _, error: Throwable? ->
                        if (error != null) {
                            result.completeExceptionally(error)
                        } else {
                            result.complete(null)
                        }
                    }
            }.exceptionally { error ->
                result.completeExceptionally(error)
                null
            }
        }
        return result
    }

    private fun joinCall(
        agent: CallAgent,
        audioOptions: OutgoingAudioOptions,
        videoOptions: OutgoingVideoOptions?,
        joinMeetingLocator: JoinMeetingLocator,
    ) {
        val joinCallOptions = JoinCallOptions()
        joinCallOptions.outgoingAudioOptions = audioOptions
        videoOptions?.let { joinCallOptions.outgoingVideoOptions = videoOptions }

        nullableCall = agent.join(context, joinMeetingLocator, joinCallOptions)
        callingSDKEventHandler.onJoinCall(call)
    }

    private fun startCall(
        agent: CallAgent,
        audioOptions: OutgoingAudioOptions,
        videoOptions: OutgoingVideoOptions?,
        participants: Collection<CommunicationIdentifier>
    ) {
        val startCallOptions = StartCallOptions()
        startCallOptions.outgoingAudioOptions = audioOptions
        videoOptions?.let { startCallOptions.outgoingVideoOptions = videoOptions }

        nullableCall = agent.startCall(context, participants, startCallOptions)
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

    private fun createDeviceManager(): CompletableFuture<DeviceManager> {
        val deviceManagerCompletableFuture = getDeviceManagerCompletableFuture()
        if (deviceManagerCompletableFuture.isCompletedExceptionally ||
            !deviceManagerCompletableFuture.isDone
        ) {
            callClient.getDeviceManager(context)
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
        return deviceManagerCompletableFuture
    }

    private fun initializeCameras(): CompletableFuture<Void> {
        if (camerasInitializedCompletableFuture == null) {
            camerasInitializedCompletableFuture = CompletableFuture<Void>()
            getDeviceManagerCompletableFuture().whenComplete { deviceManager: DeviceManager?, _: Throwable? ->

                completeCamerasInitializedCompletableFuture()
                videoDevicesUpdatedListener =
                    VideoDevicesUpdatedListener {
                        completeCamerasInitializedCompletableFuture()
                    }
                deviceManager?.addOnCamerasUpdatedListener(videoDevicesUpdatedListener)
            }
        }

        return camerasInitializedCompletableFuture!!
    }

    private fun cameraExist() = getDeviceManagerCompletableFuture().get().cameras.isNotEmpty()

    private fun completeCamerasInitializedCompletableFuture() {
        camerasCountStateFlow.value =
            getDeviceManagerCompletableFuture().get().cameras.size
        if ((isAndroidTV(context) && cameraExist()) || doFrontAndBackCamerasExist()) {
            camerasInitializedCompletableFuture?.complete(null)
        }
    }

    // predefined order to return camera
    private fun getCameraByFacingTypeSelection(): com.azure.android.communication.calling.VideoDeviceInfo? {
        listOf(
            CameraFacing.FRONT,
            CameraFacing.BACK,
            CameraFacing.EXTERNAL,
            CameraFacing.PANORAMIC,
            CameraFacing.LEFT_FRONT,
            CameraFacing.RIGHT_FRONT,
            CameraFacing.UNKNOWN
        ).forEach {
            val camera = getCamera(it)
            if (camera != null) {
                return camera
            }
        }
        return null
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

    private fun getNextCamera(deviceId: String): com.azure.android.communication.calling.VideoDeviceInfo? {
        val cameras = getDeviceManagerCompletableFuture().get().cameras
        val deviceIndex = cameras?.indexOfFirst { it.id == deviceId }
        deviceIndex?.let {
            val nextCameraIndex = (deviceIndex + 1) % cameras.size
            return cameras[nextCameraIndex]
        }
        return null
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
        nullableCall = null
        callClientInternal = null
        localVideoStreamCompletableFuture = null
        camerasInitializedCompletableFuture = null
        deviceManagerCompletableFuture = null
        endCallCompletableFuture?.complete(null)
    }

    private fun canCreateLocalVideoStream() =
        deviceManagerCompletableFuture != null || callClient != null

    private fun switchCameraAsyncAndroidTV(): CompletableFuture<CameraDeviceSelectionStatus> {
        val result = CompletableFuture<CameraDeviceSelectionStatus>()
        this.getLocalVideoStream()
            .thenAccept { videoStream: LocalVideoStream ->
                initializeCameras().thenAccept {
                    val desiredCamera = getNextCamera(videoStream.source.id)
                    if (desiredCamera == null) {
                        result.completeExceptionally(null)
                    } else {
                        videoStream.switchSource(desiredCamera.into())
                            .exceptionally {
                                result.completeExceptionally(it)
                                null
                            }.thenRun {
                                val cameraDeviceSelectionStatus =
                                    when (desiredCamera.cameraFacing) {
                                        CameraFacing.FRONT -> CameraDeviceSelectionStatus.FRONT
                                        CameraFacing.BACK -> CameraDeviceSelectionStatus.BACK
                                        CameraFacing.UNKNOWN -> CameraDeviceSelectionStatus.UNKNOWN
                                        CameraFacing.RIGHT_FRONT -> CameraDeviceSelectionStatus.RIGHT_FRONT
                                        CameraFacing.LEFT_FRONT -> CameraDeviceSelectionStatus.LEFT_FRONT
                                        CameraFacing.PANORAMIC -> CameraDeviceSelectionStatus.PANORAMIC
                                        CameraFacing.EXTERNAL -> CameraDeviceSelectionStatus.EXTERNAL
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

    private fun switchCameraAsyncMobile(): CompletableFuture<CameraDeviceSelectionStatus> {
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
                        videoStream.switchSource(desiredCamera.into())
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

    private fun onJoinCallFailed(
        startCallCompletableFuture: CompletableFuture<Void>,
        error: Throwable?,
    ): Nothing? {
        startCallCompletableFuture.completeExceptionally(error)
        return null
    }
}
