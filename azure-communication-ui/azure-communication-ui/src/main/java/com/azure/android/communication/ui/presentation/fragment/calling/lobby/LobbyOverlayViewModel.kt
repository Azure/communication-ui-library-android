// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class LobbyOverlayViewModel {
    private lateinit var displayLobbyOverlayFlow: MutableStateFlow<Boolean>
    private lateinit var isConfirmLeaveOverlayDisplayedStateFlow: MutableStateFlow<Boolean>

    fun getDisplayLobbyOverlayFlow(): StateFlow<Boolean> = displayLobbyOverlayFlow

    fun init(
        callingState: CallingStatus,
        confirmLeaveOverlayDisplayState: Boolean
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow = MutableStateFlow(displayLobbyOverlay)
        isConfirmLeaveOverlayDisplayedStateFlow = MutableStateFlow(confirmLeaveOverlayDisplayState)
    }

    fun update(
        callingState: CallingStatus
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow.value = displayLobbyOverlay
    }

    fun updateConfirmLeaveOverlayDisplayState(
        confirmLeaveOverlayDisplayState: Boolean
    ) {
        isConfirmLeaveOverlayDisplayedStateFlow.value =
            confirmLeaveOverlayDisplayState
    }

    fun getIsConfirmLeaveOverlayDisplayedStateFlow(): StateFlow<Boolean> {
        return isConfirmLeaveOverlayDisplayedStateFlow
    }

    private fun shouldDisplayLobbyOverlay(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY
}
