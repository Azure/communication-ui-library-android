// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.error

import android.util.Log
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.CALL_END_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.CALL_JOIN_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.SWITCH_CAMERA_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.TOKEN_EXPIRED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.TURN_CAMERA_OFF_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.TURN_CAMERA_ON_FAILED
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.UNKNOWN_ERROR
import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
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
                callStateError.errorCode == TOKEN_EXPIRED ||
                (fatalError != null && fatalError != lastFatalError)
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
        newCallStateError.callCompositeEventCode != CallCompositeEventCode.CALL_EVICTED &&
            newCallStateError.callCompositeEventCode != CallCompositeEventCode.CALL_DECLINED

    private fun callStateErrorCallback(callStateError: CallStateError) {
        try {
            val eventArgs =
                CallCompositeErrorEvent(
                    getCallCompositeErrorCode(callStateError.errorCode),
                    null,
                )
            configuration.callCompositeEventsHandler.getOnErrorHandlers().forEach { it.handle(eventArgs) }
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
                CallCompositeErrorEvent(
                    getCallCompositeErrorCode(error.errorCode),
                    error.fatalError,
                )
            configuration.callCompositeEventsHandler.getOnErrorHandlers().forEach { it.handle(eventArgs) }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }

    private fun getCallCompositeErrorCode(errorCode: ErrorCode?): CallCompositeErrorCode? {
        Log.d("Mohtasim", "Error code - " + errorCode.toString())
        errorCode?.let {
            when (it) {
                TOKEN_EXPIRED -> {
                    return CallCompositeErrorCode.TOKEN_EXPIRED
                }
                UNKNOWN_ERROR -> {
                    return CallCompositeErrorCode.UNKNOWN_ERROR
                }
                CALL_JOIN_FAILED -> {
                    return CallCompositeErrorCode.CALL_JOIN_FAILED
                }
                CALL_END_FAILED -> {
                    return CallCompositeErrorCode.CALL_END_FAILED
                }
                SWITCH_CAMERA_FAILED, TURN_CAMERA_ON_FAILED, TURN_CAMERA_OFF_FAILED -> {
                    return CallCompositeErrorCode.CAMERA_FAILURE
                }
                else -> {
                    return null
                }
            }
        }

        return null
    }
}
