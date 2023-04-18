// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeCallState
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CallStateHandler(
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
) {
    private var callingStatusStateFlow = MutableStateFlow<CallingStatus?>(null)

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                callingStatusStateFlow.value = it.callState.callingStatus
            }
        }
        coroutineScope.launch {
            callingStatusStateFlow.collect {
                it?.let {
                    sendCallStateChangedEvent(it)
                }
            }
        }
    }

    fun getCallCompositeCallState(): CallCompositeCallState {
        return store.getStateFlow().value.callState.callingStatus.callCompositeCallState()
    }

    private fun sendCallStateChangedEvent(status: CallingStatus) {
        try {
            configuration.callCompositeEventsHandler.getCallStateHandler().forEach {
                it.handle(CallCompositeCallStateEvent(status.callCompositeCallState()))
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}

internal fun CallingStatus.callCompositeCallState(): CallCompositeCallState {
    return when (this) {
        CallingStatus.CONNECTED -> CallCompositeCallState.CONNECTED
        CallingStatus.CONNECTING -> CallCompositeCallState.CONNECTING
        CallingStatus.DISCONNECTED -> CallCompositeCallState.DISCONNECTED
        CallingStatus.DISCONNECTING -> CallCompositeCallState.DISCONNECTING
        CallingStatus.EARLY_MEDIA -> CallCompositeCallState.EARLY_MEDIA
        CallingStatus.RINGING -> CallCompositeCallState.RINGING
        CallingStatus.LOCAL_HOLD -> CallCompositeCallState.LOCAL_HOLD
        CallingStatus.IN_LOBBY -> CallCompositeCallState.IN_LOBBY
        CallingStatus.REMOTE_HOLD -> CallCompositeCallState.REMOTE_HOLD
        else -> CallCompositeCallState.NONE
    }
}
