// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import android.view.View
import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.CallingAction

internal class ConfirmLeaveOverlayViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private var isConfirmLeaveOverlayDisplayed: Int = View.INVISIBLE

    fun setConfirmLeaveOverlayState(confirmLeaveOverlayState: Int) {
        isConfirmLeaveOverlayDisplayed = confirmLeaveOverlayState
    }

    fun getConfirmLeaveOverlayState(): Int {
        return isConfirmLeaveOverlayDisplayed
    }

    fun confirm() {
        dispatchAction(action = CallingAction.CallEndRequested())
    }

    fun cancel() {
        setConfirmLeaveOverlayState(View.INVISIBLE)
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
