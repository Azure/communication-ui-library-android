// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeExitEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CompositeManager(
    private val store: Store<ReduxState>,
    private val configuration: CallCompositeConfiguration,
    coroutineContextProvider: CoroutineContextProvider,
) {
    companion object {
        var MAX_WAIT_FOR_EXIT = 3000L
    }
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private var callingStatusStateFlow = MutableStateFlow<CallingStatus?>(null)

    fun exit() {
        val callIsNotInProgress =
            store.getCurrentState().callState.callingStatus == CallingStatus.NONE ||
                store.getCurrentState().callState.callingStatus == CallingStatus.DISCONNECTED

        // if call state is none or Disconnected exit composite
        if (callIsNotInProgress) {
            notifyCompositeExit()
            store.dispatch(action = NavigationAction.Exit())
        } else {
            // subscribe to redux state change
            coroutineScope.launch {
                store.getStateFlow().collect {
                    callingStatusStateFlow.value = it.callState.callingStatus
                }
            }

            // subscribe to call state change
            coroutineScope.launch {
                callingStatusStateFlow.collect { callingStatus ->
                    callingStatus?.let {
                        if (callingStatus == CallingStatus.DISCONNECTED || callingStatus == CallingStatus.NONE) {
                            notifyCompositeExit()
                        }
                    }
                }
            }

            // end call
            store.dispatch(action = CallingAction.CallEndRequested())

            // it is possible that because of any error like network errors etc call state is not received from SDK
            // in this case force exit composite after 3 seconds
            coroutineScope.launch {
                delay(MAX_WAIT_FOR_EXIT)
                if (store.getCurrentState().callState.callingStatus != CallingStatus.DISCONNECTED) {
                    store.dispatch(action = NavigationAction.Exit())
                    notifyCompositeExit()
                }
            }
        }
    }

    private fun notifyCompositeExit() {
        configuration.callCompositeEventsHandler.getOnExitEventHandlers().forEach {
            val eventArgs =
                CallCompositeExitEvent(null)
            it.handle(eventArgs)
        }
        dispose()
    }

    private fun dispose() {
        coroutineScope.cancel()
    }
}
