// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service.calling

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.ui.configuration.events.CommunicationUIErrorCode
import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.model.CallInfoModel
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CallingService(
    private val callingSDKWrapper: CallingSDKWrapper,
    coroutineContextProvider: CoroutineContextProvider,
) {
    companion object {
        private const val LOCAL_VIDEO_STREAM_ID = "BuiltInCameraVideoStream"
        private const val CALL_END_REASON_TOKEN_EXPIRED = 401
        private const val CALL_END_REASON_SUCCESS = 0

        /*
        * Call canceled, locally declined, ended due to an endpoint mismatch issue, or failed to generate media offer.
        * Expected behavior.
        * */
        private const val CALL_END_REASON_CANCELED = 487

        /*
        * Call globally declined by remote Communication Services participant.
        * Expected behavior.
        * */
        private const val CALL_END_REASON_DECLINED = 603
    }

    private val participantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()
    private val isMutedSharedFlow = MutableSharedFlow<Boolean>()
    private val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
    private val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private var callInfoModelSharedFlow = MutableSharedFlow<CallInfoModel>()
    private var callingStatus: CallingStatus = CallingStatus.NONE

    fun turnCameraOn(): CompletableFuture<String> {
        return callingSDKWrapper.turnOnVideoAsync().thenApply { stream ->
            stream?.source?.id
        }
    }

    fun turnCameraOff(): CompletableFuture<Void> {
        return callingSDKWrapper.turnOffVideoAsync()
    }

    fun switchCamera(): CompletableFuture<CameraDeviceSelectionStatus> {
        return callingSDKWrapper.switchCameraAsync()
    }

    fun turnMicOff(): CompletableFuture<Void> {
        return callingSDKWrapper.turnOffMicAsync()
    }

    fun turnMicOn(): CompletableFuture<Void> {
        return callingSDKWrapper.turnOnMicAsync()
    }

    fun turnLocalCameraOn(): CompletableFuture<String> {
        return callingSDKWrapper.getLocalVideoStream().thenApply {
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

    fun getIsMutedSharedFlow(): Flow<Boolean> {
        return isMutedSharedFlow
    }

    fun getIsRecordingSharedFlow(): Flow<Boolean> {
        return isRecordingSharedFlow
    }

    fun getCallInfoModelEventSharedFlow(): SharedFlow<CallInfoModel> = callInfoModelSharedFlow

    fun getIsTranscribingSharedFlow(): Flow<Boolean> {
        return isTranscribingSharedFlow
    }

    fun endCall(): CompletableFuture<Void> {
        return callingSDKWrapper.endCall()
    }

    fun setupCall() {
        callingSDKWrapper.setupCall()
    }

    fun dispose() {
        coroutineScope.cancel()
        callingSDKWrapper.dispose()
    }

    fun startCall(cameraState: CameraState, audioState: AudioState): CompletableFuture<Void> {
        coroutineScope.launch {
            callingSDKWrapper.getCallingStateWrapperSharedFlow().collect {
                val callStateError = when (it.callEndReason) {
                    CALL_END_REASON_SUCCESS, CALL_END_REASON_CANCELED, CALL_END_REASON_DECLINED -> null
                    CALL_END_REASON_TOKEN_EXPIRED -> CallStateError(CommunicationUIErrorCode.TOKEN_EXPIRED)
                    else -> {
                        if (callingStatus == CallingStatus.CONNECTED) {
                            CallStateError(CommunicationUIErrorCode.CALL_END)
                        } else {
                            CallStateError(CommunicationUIErrorCode.CALL_JOIN)
                        }
                    }
                }
                callingStatus = getCallingState(it.callState)
                callInfoModelSharedFlow.emit(CallInfoModel(callingStatus, callStateError))
            }
        }

        coroutineScope.launch {
            callingSDKWrapper.getIsMutedSharedFlow().collect {
                isMutedSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSDKWrapper.getRemoteParticipantInfoModelSharedFlow().collect {
                participantsInfoModelSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSDKWrapper.getIsRecordingSharedFlow().collect {
                isRecordingSharedFlow.emit(it)
            }
        }

        coroutineScope.launch {
            callingSDKWrapper.getIsTranscribingSharedFlow().collect {
                isTranscribingSharedFlow.emit(it)
            }
        }

        return callingSDKWrapper.startCall(cameraState, audioState)
    }

    private fun getCallingState(callState: CallState): CallingStatus {
        when (callState) {
            CallState.CONNECTED -> {
                return CallingStatus.CONNECTED
            }
            CallState.CONNECTING -> {
                return CallingStatus.CONNECTING
            }
            CallState.DISCONNECTED -> {
                return CallingStatus.DISCONNECTED
            }
            CallState.DISCONNECTING -> {
                return CallingStatus.DISCONNECTING
            }
            CallState.EARLY_MEDIA -> {
                return CallingStatus.EARLY_MEDIA
            }
            CallState.RINGING -> {
                return CallingStatus.RINGING
            }
            CallState.LOCAL_HOLD -> {
                return CallingStatus.LOCAL_HOLD
            }
            CallState.IN_LOBBY -> {
                return CallingStatus.IN_LOBBY
            }
            CallState.REMOTE_HOLD -> {
                return CallingStatus.REMOTE_HOLD
            }
            else -> return CallingStatus.NONE
        }
    }
}
