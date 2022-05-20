// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CommunicationUIErrorEvent
import com.azure.android.communication.ui.calling.models.CommunicationUIErrorCode.TOKEN_EXPIRED
import com.azure.android.communication.ui.calling.models.CommunicationUIEventCode
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

        if (isEmergencyExit(state.errorState)) { store.dispatch(ErrorAction.EmergencyExit()) }
    }

    private fun isEmergencyExit(errorState: ErrorState) =
        errorState.run {
            callStateError != null &&
                callStateError != lastCallStateError &&
                callStateError.communicationUIErrorCode == TOKEN_EXPIRED ||
                (fatalError != null && fatalError != lastFatalError)
        }

    private fun checkIfCallingCompositeExceptionIsNewAndNotify(
        newError: CallCompositeError?,
        oldError: CallCompositeError?,
        function: (CallCompositeError) -> Unit,
    ) {
        if (newError != null && newError != oldError) {
            function(newError)
            try {
                val eventArgs =
                    CommunicationUIErrorEvent(
                        newError.communicationUIErrorCode,
                        newError.cause,
                    )
                configuration.callCompositeEventsHandler.getOnErrorHandler()?.handle(eventArgs)
            } catch (error: Throwable) {
                // suppress any possible application errors
            }
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
        newCallStateError.communicationUIEventCode != CommunicationUIEventCode.CALL_EVICTED &&
            newCallStateError.communicationUIEventCode != CommunicationUIEventCode.CALL_DECLINED

    private fun callStateErrorCallback(callStateError: CallStateError) {
        try {
            val eventArgs =
                CommunicationUIErrorEvent(
                    callStateError.communicationUIErrorCode,
                    null,
                )
            configuration.callCompositeEventsHandler.getOnErrorHandler()?.handle(eventArgs)
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
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
                    error.codeCallComposite,
                    error.fatalError,
                )
            configuration.callCompositeEventsHandler.getOnErrorHandler()?.handle(eventArgs)
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
