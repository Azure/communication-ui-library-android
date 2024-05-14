// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CallStateHandler(
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
) {
    private var lastSentCallingStatus: CallingStatus? = null

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect { state ->
                onStateChange(state)
            }
        }
    }

    fun getCallCompositeCallState(): CallCompositeCallStateCode {
        return store.getStateFlow().value.callState.callingStatus.callCompositeCallState()
    }

    // make sure to notify Contoso about call state before exiting the composite
    // This helps to fix race condition when disconnected call is notified after exiting the composite
    fun onCompositeExit() {
        val currentState = store.getCurrentState()
        onStateChange(currentState)
    }

    private fun onStateChange(currentState: ReduxState) {
        if (lastSentCallingStatus != currentState.callState.callingStatus) {
            lastSentCallingStatus = currentState.callState.callingStatus
            lastSentCallingStatus?.let {
                sendCallStateChangedEvent(
                    it,
                    currentState.callState.callId,
                    currentState.callState.callEndReasonCode,
                    currentState.callState.callEndReasonSubCode,
                )
            }
        }
    }

    private fun sendCallStateChangedEvent(
        status: CallingStatus,
        callID: String?,
        callEndReasonCode: Int?,
        callEndReasonSubCode: Int?,
    ) {
        try {
            configuration.callCompositeEventsHandler.getCallStateHandler().forEach {
                it.handle(
                    CallCompositeCallStateChangedEvent(
                        status.callCompositeCallState(),
                        callEndReasonCode ?: 0,
                        callEndReasonSubCode ?: 0,
                        callID
                    )
                )
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}

internal fun CallingStatus.callCompositeCallState(): CallCompositeCallStateCode {
    return when (this) {
        CallingStatus.CONNECTED -> CallCompositeCallStateCode.CONNECTED
        CallingStatus.CONNECTING -> CallCompositeCallStateCode.CONNECTING
        CallingStatus.DISCONNECTED -> CallCompositeCallStateCode.DISCONNECTED
        CallingStatus.DISCONNECTING -> CallCompositeCallStateCode.DISCONNECTING
        CallingStatus.EARLY_MEDIA -> CallCompositeCallStateCode.EARLY_MEDIA
        CallingStatus.RINGING -> CallCompositeCallStateCode.RINGING
        CallingStatus.LOCAL_HOLD -> CallCompositeCallStateCode.LOCAL_HOLD
        CallingStatus.IN_LOBBY -> CallCompositeCallStateCode.IN_LOBBY
        CallingStatus.REMOTE_HOLD -> CallCompositeCallStateCode.REMOTE_HOLD
        else -> CallCompositeCallStateCode.NONE
    }
}
