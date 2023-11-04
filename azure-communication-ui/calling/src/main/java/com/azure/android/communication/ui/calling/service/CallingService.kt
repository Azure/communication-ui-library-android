// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallInfoModel
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CallingService(
    private val callingSdk: CallingSDK,
    coroutineContextProvider: CoroutineContextProvider,
    private val logger: Logger? = null,
) {
    companion object {
        private const val LOCAL_VIDEO_STREAM_ID = "BuiltInCameraVideoStream"
    }

    private val participantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()
    private val isMutedSharedFlow = MutableSharedFlow<Boolean>()
    private val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
    private val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
    private val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private var callInfoModelSharedFlow = MutableSharedFlow<CallInfoModel>()
    private var callIdStateFlow = MutableStateFlow<String?>(null)
    private var callingStatus: CallingStatus = CallingStatus.NONE

    fun registerPushNotification(deviceRegistrationToken: String): CompletableFuture<Void> {
        return callingSdk.registerPushNotification(deviceRegistrationToken)
    }

    //region Call Diagnostics
    private val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
    private val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
    private val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
    //endregion

    fun turnCameraOn(): CompletableFuture<String> {
        return callingSdk.turnOnVideoAsync().thenApply { stream ->
            stream?.source?.id
        }
    }

    fun turnCameraOff(): CompletableFuture<Void> {
        return callingSdk.turnOffVideoAsync()
    }

    fun switchCamera(): CompletableFuture<CameraDeviceSelectionStatus> {
        return callingSdk.switchCameraAsync()
    }

    fun turnMicOff(): CompletableFuture<Void> {
        return callingSdk.turnOffMicAsync()
    }

    fun turnMicOn(): CompletableFuture<Void> {
        return callingSdk.turnOnMicAsync()
    }

    fun startAudio() {
        callingSdk.startAudio()
    }

    fun stopAudio() {
        callingSdk.stopAudio()
    }

    fun turnLocalCameraOn(): CompletableFuture<String> {
        return callingSdk.getLocalVideoStream().thenApply {
            /**
             * On switch camera video stream ID is changed
             * We do not sync local video stream ID to store on camera switch
             * Team decided to use single ID for local video stream
             */
            LOCAL_VIDEO_STREAM_ID
        }
    }

    fun getParticipantsInfoModelSharedFlow(): SharedFlow<Map<String, ParticipantInfoModel>> {
        return participantsInfoModelSharedFlow
    }

    fun getDominantSpeakersSharedFlow(): SharedFlow<List<String>> {
        return dominantSpeakersSharedFlow
    }

    fun getIsMutedSharedFlow(): Flow<Boolean> {
        return isMutedSharedFlow
    }

    fun getIsRecordingSharedFlow(): Flow<Boolean> {
        return isRecordingSharedFlow
    }

    fun getCallInfoModelEventSharedFlow(): SharedFlow<CallInfoModel> = callInfoModelSharedFlow

    fun getCallIdStateFlow(): SharedFlow<String?> = callIdStateFlow

    fun getIsTranscribingSharedFlow(): Flow<Boolean> {
        return isTranscribingSharedFlow
    }

    //region Call Diagnostics
    fun getNetworkQualityCallDiagnosticsFlow(): Flow<NetworkQualityCallDiagnosticModel> {
        return networkQualityCallDiagnosticsSharedFlow
    }

    fun getNetworkCallDiagnosticsFlow(): Flow<NetworkCallDiagnosticModel> {
        return networkCallDiagnosticsSharedFlow
    }

    fun getMediaCallDiagnosticsFlow(): Flow<MediaCallDiagnosticModel> {
        return mediaCallDiagnosticsSharedFlow
    }
    //endregion

    fun getCamerasCountStateFlow() = callingSdk.getCamerasCountStateFlow()

    fun endCall(): CompletableFuture<Void> {
        return callingSdk.endCall()
    }

    fun hold(): CompletableFuture<Void> {
        return callingSdk.hold()
    }

    fun resume(): CompletableFuture<Void> {
        return callingSdk.resume()
    }

    fun setupCall(): CompletableFuture<Void> {
        return callingSdk.setupCall()
    }

    fun dispose() {
        coroutineScope.cancel()
        callingSdk.dispose()
    }

    fun startCall(cameraState: CameraState, audioState: AudioState): CompletableFuture<Void> {
        coroutineScope.launch {
            callingSdk.getCallingStateWrapperSharedFlow().collect {
                logger?.debug(it.toString())
                val callStateError = it.asCallStateError(currentStatus = callingStatus)
                callingStatus = it.toCallingStatus()
                callInfoModelSharedFlow.emit(CallInfoModel(callingStatus, callStateError))
            }
        }

        coroutineScope.launch {
            callingSdk.getCallIdStateFlow().collect {
                callIdStateFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSdk.getIsMutedSharedFlow().collect {
                isMutedSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSdk.getRemoteParticipantInfoModelSharedFlow().collect {
                participantsInfoModelSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSdk.getIsRecordingSharedFlow().collect {
                isRecordingSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSdk.getIsTranscribingSharedFlow().collect {
                isTranscribingSharedFlow.emit(it)
            }
        }

        //region Call Diagnostics
        coroutineScope.launch {
            callingSdk.getNetworkQualityCallDiagnosticSharedFlow().collect {
                networkQualityCallDiagnosticsSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSdk.getNetworkCallDiagnosticSharedFlow().collect {
                networkCallDiagnosticsSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSdk.getMediaCallDiagnosticSharedFlow().collect {
                mediaCallDiagnosticsSharedFlow.emit(it)
            }
        }
        //endregion

        coroutineScope.launch {
            callingSdk.getDominantSpeakersSharedFlow().collect {
                dominantSpeakersSharedFlow.emit(it.speakers)
            }
        }

        return callingSdk.startCall(cameraState, audioState)
    }
}
