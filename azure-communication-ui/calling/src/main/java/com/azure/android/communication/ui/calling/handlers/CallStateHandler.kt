// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CallStateHandler(
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
) {
    private var lastSentCallStatus: CallStatus? = null

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect { state ->
                if (lastSentCallStatus != state.callState.callStatus) {
                    lastSentCallStatus = state.callState.callStatus
                    lastSentCallStatus?.let { sendCallStateChangedEvent(it) }
                }
            }
        }
    }

    fun getCallCompositeCallState(): CallCompositeCallStateCode {
        return store.getStateFlow().value.callState.callStatus.callCompositeCallState()
    }

    private fun sendCallStateChangedEvent(status: CallStatus) {
        try {
            configuration.callCompositeEventsHandler.getCallStateHandler().forEach {
                it.handle(CallCompositeCallStateChangedEvent(status.callCompositeCallState()))
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}

internal fun CallStatus.callCompositeCallState(): CallCompositeCallStateCode {
    return when (this) {
        CallStatus.CONNECTED -> CallCompositeCallStateCode.CONNECTED
        CallStatus.CONNECTING -> CallCompositeCallStateCode.CONNECTING
        CallStatus.DISCONNECTED -> CallCompositeCallStateCode.DISCONNECTED
        CallStatus.DISCONNECTING -> CallCompositeCallStateCode.DISCONNECTING
        CallStatus.EARLY_MEDIA -> CallCompositeCallStateCode.EARLY_MEDIA
        CallStatus.RINGING -> CallCompositeCallStateCode.RINGING
        CallStatus.LOCAL_HOLD -> CallCompositeCallStateCode.LOCAL_HOLD
        CallStatus.IN_LOBBY -> CallCompositeCallStateCode.IN_LOBBY
        CallStatus.REMOTE_HOLD -> CallCompositeCallStateCode.REMOTE_HOLD
        else -> CallCompositeCallStateCode.NONE
    }
}
