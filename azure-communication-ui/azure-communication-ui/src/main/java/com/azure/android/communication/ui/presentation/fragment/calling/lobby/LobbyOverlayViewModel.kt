// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class LobbyOverlayViewModel {
    private lateinit var displayLobbyOverlayFlow: MutableStateFlow<Boolean>

    fun getDisplayLobbyOverlayFlow(): StateFlow<Boolean> = displayLobbyOverlayFlow

    fun init(
        callingState: CallingStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow = MutableStateFlow(displayLobbyOverlay)
    }

    fun update(
        callingState: CallingStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow.value = displayLobbyOverlay
    }

    private fun shouldDisplayLobbyOverlay(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY
}
