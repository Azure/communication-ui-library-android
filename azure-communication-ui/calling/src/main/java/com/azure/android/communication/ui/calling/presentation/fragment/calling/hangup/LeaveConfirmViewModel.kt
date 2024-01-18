// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import android.content.Context
import android.os.Build
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.telecom.TelecomConnectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class LeaveConfirmViewModel(private val store: Store<ReduxState>) {
    private val shouldDisplayLeaveConfirmMutableStateFlow = MutableStateFlow(false)
    var shouldDisplayLeaveConfirmStateFlow = shouldDisplayLeaveConfirmMutableStateFlow as StateFlow<Boolean>

    fun getShouldDisplayLeaveConfirmFlow(): StateFlow<Boolean> {
        return shouldDisplayLeaveConfirmStateFlow
    }

    fun confirm(context: Context) {
        if (store.getCurrentState().callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN &&
            store.getCurrentState().callState.callingStatus != CallingStatus.CONNECTED
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
