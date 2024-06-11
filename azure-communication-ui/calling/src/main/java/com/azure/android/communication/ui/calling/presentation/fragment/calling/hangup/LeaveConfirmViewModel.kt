// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class LeaveConfirmViewModel(private val store: Store<ReduxState>) {
    private val shouldDisplayLeaveConfirmMutableStateFlow = MutableStateFlow(false)
    var shouldDisplayLeaveConfirmStateFlow = shouldDisplayLeaveConfirmMutableStateFlow as StateFlow<Boolean>

    fun getShouldDisplayLeaveConfirmFlow(): StateFlow<Boolean> {
        return shouldDisplayLeaveConfirmStateFlow
    }

    fun update(visibilityState: VisibilityState) {
        if (visibilityState.status != VisibilityStatus.VISIBLE)
            cancel()
    }

    fun confirm() {
        if (store.getCurrentState().localParticipantState.initialCallJoinState.skipSetupScreen &&
            (
                store.getCurrentState().callState.callingStatus != CallingStatus.CONNECTED &&
                    store.getCurrentState().callState.callingStatus != CallingStatus.CONNECTING &&
                    store.getCurrentState().callState.callingStatus != CallingStatus.RINGING
                )
        ) {
            dispatchAction(action = NavigationAction.Exit())
        } else {
            dispatchAction(action = CallingAction.CallEndRequested())
        }
    }

    fun cancel() {
        shouldDisplayLeaveConfirmMutableStateFlow.value = false
    }

    fun requestExitConfirmation() {
        shouldDisplayLeaveConfirmMutableStateFlow.value = true
    }

    private fun dispatchAction(action: Action) {
        store.dispatch(action)
    }
}
