// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.ui.calling.models.CallCompositeEventCode.Companion.CALL_EVICTED
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.CALL_END_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.CALL_JOIN_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.TOKEN_EXPIRED
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallInfoModel
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode.Companion.CALL_DECLINED
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.calling.service.sdk.CallingStateWrapper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
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
    private val logger: Logger? = null,
) {
    companion object {
        private const val LOCAL_VIDEO_STREAM_ID = "BuiltInCameraVideoStream"
        internal const val CALL_END_REASON_TOKEN_EXPIRED = 401
        internal const val CALL_END_REASON_SUCCESS = 0

        /*
        * Call canceled, locally declined, ended due to an endpoint mismatch issue, or failed to generate media offer.
        * Expected behavior.
        * */
        internal const val CALL_END_REASON_CANCELED = 487

        /*
        * Call globally declined by remote Communication Services participant.
        * Expected behavior.
        * */
        internal const val CALL_END_REASON_DECLINED = 603
        internal const val CALL_END_REASON_TEAMS_EVICTED = 5300
        internal const val CALL_END_REASON_EVICTED = 5000
        internal const val CALL_END_REASON_SUB_CODE_DECLINED = 5854
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

    fun hold(): CompletableFuture<Void> {
        return callingSDKWrapper.hold()
    }

    fun resume(): CompletableFuture<Void> {
        return callingSDKWrapper.resume()
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
                val callStateError = getCallStateError(it)
                callingStatus = it.toCallingStatus()
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

    private fun getCallStateError(callingState: CallingStateWrapper): CallStateError? =
        callingState.run {
            logger?.debug(callingState.toString())
            when {
                callingState.isEvicted() -> CallStateError(CALL_END_FAILED, CALL_EVICTED)
                callingState.isDeclined() -> CallStateError(CALL_END_FAILED, CALL_DECLINED)
                callEndReason == CALL_END_REASON_SUCCESS ||
                    callEndReason == CALL_END_REASON_CANCELED ||
                    callEndReason == CALL_END_REASON_DECLINED -> null
                callEndReason == CALL_END_REASON_TOKEN_EXPIRED ->
                    CallStateError(TOKEN_EXPIRED, null)
                else -> {
                    if (callingStatus == CallingStatus.CONNECTED) {
                        CallStateError(CALL_END_FAILED, null)
                    } else {
                        CallStateError(CALL_JOIN_FAILED, null)
                    }
                }
            }
        }
}
