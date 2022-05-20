// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hold

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class HoldOverlayViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var displayHoldOverlayFlow: MutableStateFlow<Boolean>

    fun getDisplayHoldOverlayFlow(): StateFlow<Boolean> = displayHoldOverlayFlow

    fun init(
        callingState: CallingStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayHoldOverlay(callingState)
        displayHoldOverlayFlow = MutableStateFlow(displayLobbyOverlay)
    }

    fun update(
        callingState: CallingStatus,
    ) {
        val displayHoldOverlay = shouldDisplayHoldOverlay(callingState)
        displayHoldOverlayFlow.value = displayHoldOverlay
    }

    private fun shouldDisplayHoldOverlay(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.LOCAL_HOLD

    fun resumeCall() {
        dispatch(CallingAction.ResumeRequested())
    }
}
