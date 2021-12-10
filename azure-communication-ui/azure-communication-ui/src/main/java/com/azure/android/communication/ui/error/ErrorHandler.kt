// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.error

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.events.ErrorEvent
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.ErrorAction
import com.azure.android.communication.ui.redux.state.ReduxState
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

        val fireEmergencyExit = isEmergencyExit(state)

        checkIfFatalErrorIsNewAndNotify(
            state.errorState.fatalError,
            lastFatalError,
        ) {
            lastFatalError = it
        }

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

    private fun isEmergencyExit(state: ReduxState) =
        (
            state.errorState.fatalError != null &&
                state.errorState.fatalError != lastFatalError
            ) ||
            (
                state.errorState.callStateError != null &&
                    state.errorState.callStateError != lastCallStateError &&
                    state.errorState.callStateError?.callCompositeErrorCode == CallCompositeErrorCode.TOKEN_EXPIRED
                )

    private fun checkIfCallingCompositeExceptionIsNewAndNotify(
        newError: CallCompositeError?,
        oldError: CallCompositeError?,
        function: (CallCompositeError) -> Unit,
    ) {
        if (newError != null && newError != oldError) {
            function(newError)
            try {
                val eventArgs =
                    ErrorEvent(
                        newError.callCompositeErrorCode,
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
            function(newCallStateError)
            callStateErrorCallback(newCallStateError)
        }
    }

    private fun callStateErrorCallback(callStateError: CallStateError) {
        try {
            val eventArgs =
                ErrorEvent(
                    callStateError.callCompositeErrorCode,
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
                ErrorEvent<CallCompositeErrorCode>(
                    error.codeCallComposite,
                    error.fatalError,
                )
            configuration.callCompositeEventsHandler.getOnErrorHandler()?.handle(eventArgs)
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
