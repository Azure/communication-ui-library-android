// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.reduxkotlin.Dispatcher

internal class LeaveConfirmViewModel(private val dispatch: Dispatcher) {
    private val shouldDisplayLeaveConfirmMutableStateFlow = MutableStateFlow(false)
    var shouldDisplayLeaveConfirmStateFlow = shouldDisplayLeaveConfirmMutableStateFlow as StateFlow<Boolean>

    fun getShouldDisplayLeaveConfirmFlow(): StateFlow<Boolean> {
        return shouldDisplayLeaveConfirmStateFlow
    }

    fun confirm() {
        dispatchAction(action = CallingAction.CallEndRequested())
    }

    fun cancel() {
        shouldDisplayLeaveConfirmMutableStateFlow.value = false
    }

    fun requestExitConfirmation() {
        shouldDisplayLeaveConfirmMutableStateFlow.value = true
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
