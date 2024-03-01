// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class CompositeExitManager(
    private val store: Store<ReduxState>,
    private val configuration: CallCompositeConfiguration
) {

    fun onCompositeDestroy() {
        try {
            notifyCompositeExit()
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }

    fun exit() {
        val callIsNotInProgress =
            store.getCurrentState().callState.callingStatus == CallingStatus.NONE ||
                store.getCurrentState().callState.callingStatus == CallingStatus.DISCONNECTED

        // if call state is none or Disconnected exit composite
        if (callIsNotInProgress) {
            store.dispatch(action = NavigationAction.Exit())
        } else {
            // end call
            store.dispatch(action = CallingAction.CallEndRequested())
        }
    }

    private fun notifyCompositeExit() {
        configuration.callCompositeEventsHandler.getOnExitEventHandlers().forEach {
            val eventArgs =
                CallCompositeDismissedEvent(
                    store.getCurrentState().errorState.fatalError?.errorCode?.toCallCompositeErrorCode(),
                    store.getCurrentState().errorState.fatalError?.fatalError
                )
            it.handle(eventArgs)
        }
    }
}
