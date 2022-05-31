// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CommunicationUIErrorCode
import com.azure.android.communication.ui.calling.models.CommunicationUIErrorEvent
import com.azure.android.communication.ui.calling.models.EventCode
import com.azure.android.communication.ui.calling.models.internal.ErrorCode
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class ErrorHandler(
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
) {

    private var lastFatalError: FatalError? = null
    private var lastCameraError: CallCompositeError? = null
    private var lastMicError: CallCompositeError? = null
    private var lastCallStateError: CallStateError? = null

    suspend fun start() {
        store.getStateFlow().collect {
            onStateChanged(it)
        }
    }

    private fun onStateChanged(state: ReduxState) {
        val fireEmergencyExit = isEmergencyExit(state.errorState)

        checkIfFatalErrorIsNewAndNotify(
            state.errorState.fatalError,
            lastFatalError,
        ) { lastFatalError = it }

        checkIfCallingCompositeExceptionIsNewAndNotify(
            state.localParticipantState.cameraState.error,
            lastCameraError,
        ) { lastCameraError = it }

        checkIfCallingCompositeExceptionIsNewAndNotify(
            state.localParticipantState.audioState.error,
            lastMicError,
        ) { lastMicError = it }

        checkIfCallStateErrorIsNewAndNotify(
            state.errorState.callStateError,
            lastCallStateError,
        ) { lastCallStateError = it }

        if (fireEmergencyExit) {
            store.dispatch(ErrorAction.EmergencyExit())
        }
    }

    private fun isEmergencyExit(errorState: ErrorState) =
        errorState.run {
            callStateError != null &&
                callStateError != lastCallStateError &&
                callStateError.errorCode == ErrorCode.TOKEN_EXPIRED ||
                (fatalError != null && fatalError != lastFatalError)
        }

    private fun checkIfCallingCompositeExceptionIsNewAndNotify(
        newError: CallCompositeError?,
        oldError: CallCompositeError?,
        function: (CallCompositeError) -> Unit,
    ) {
        if (newError != null && newError != oldError) {
            function(newError)
        }
    }

    private fun checkIfCallStateErrorIsNewAndNotify(
        newCallStateError: CallStateError?,
        lastCallStateError: CallStateError?,
        function: (CallStateError) -> Unit,
    ) {
        if (newCallStateError != null && newCallStateError != lastCallStateError) {
            if (shouldNotifyError(newCallStateError)) {
                function(newCallStateError)
                callStateErrorCallback(newCallStateError)
            }
        }
    }

    private fun shouldNotifyError(newCallStateError: CallStateError) =
        newCallStateError.eventCode != EventCode.CALL_EVICTED &&
            newCallStateError.eventCode != EventCode.CALL_DECLINED

    private fun callStateErrorCallback(callStateError: CallStateError) {
        try {
            getCommunicationUIErrorCode(callStateError.errorCode)?.let {
                val eventArgs =
                    CommunicationUIErrorEvent(
                        it,
                        null,
                    )
                configuration.callCompositeEventsHandler.getOnErrorHandler()?.handle(eventArgs)
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }

    private fun getCommunicationUIErrorCode(errorCode: ErrorCode?): CommunicationUIErrorCode? {
        errorCode?.let {
            when (it) {
                ErrorCode.TOKEN_EXPIRED -> {
                    return CommunicationUIErrorCode.TOKEN_EXPIRED
                }
                ErrorCode.CALL_JOIN_FAILED -> {
                    return CommunicationUIErrorCode.CALL_JOIN_FAILED
                }
                ErrorCode.CALL_END_FAILED -> {
                    return CommunicationUIErrorCode.CALL_END_FAILED
                }
                else -> return null
            }
        }
        return null
    }

    private fun checkIfFatalErrorIsNewAndNotify(
        newError: FatalError?,
        oldError: FatalError?,
        updateLastSavedError: (FatalError) -> Unit,
    ) {
        if (newError != null && newError != oldError) {
            updateLastSavedError(newError)
            callErrorCallback(newError)
        }
    }

    private fun callErrorCallback(error: FatalError) {
        try {
            val eventArgs =
                CommunicationUIErrorEvent(
                    getCommunicationUIErrorCode(error.errorCode),
                    error.fatalError,
                )
            configuration.callCompositeEventsHandler.getOnErrorHandler()?.handle(eventArgs)
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
