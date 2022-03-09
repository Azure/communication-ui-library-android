// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.DisplayAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ConfirmLeaveOverlayViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private lateinit var isConfirmLeaveOverlayDisplayedStateFlow: MutableStateFlow<Boolean>

    fun init(
        confirmLeaveOverlayDisplayState: Boolean
    ) {
        isConfirmLeaveOverlayDisplayedStateFlow =
            MutableStateFlow(confirmLeaveOverlayDisplayState)
    }

    fun updateConfirmLeaveOverlayDisplayState(confirmLeaveOverlayDisplayState: Boolean) {
        isConfirmLeaveOverlayDisplayedStateFlow.value =
            confirmLeaveOverlayDisplayState
    }

    fun getIsConfirmLeaveOverlayDisplayedStateFlow(): StateFlow<Boolean> {
        return isConfirmLeaveOverlayDisplayedStateFlow
    }

    fun confirm() {
        dispatchAction(action = CallingAction.CallEndRequested())
    }

    fun cancel() {
        dispatchAction(action = DisplayAction.IsConfirmLeaveOverlayDisplayed(false))
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
