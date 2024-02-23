// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.redux.state.CallStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class WaitingLobbyOverlayViewModel {
    private lateinit var displayLobbyOverlayFlow: MutableStateFlow<Boolean>

    fun getDisplayLobbyOverlayFlow(): StateFlow<Boolean> = displayLobbyOverlayFlow

    fun init(
        callingState: CallStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow = MutableStateFlow(displayLobbyOverlay)
    }

    fun update(
        callingState: CallStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow.value = displayLobbyOverlay
    }

    private fun shouldDisplayLobbyOverlay(callStatus: CallStatus) =
        callStatus == CallStatus.IN_LOBBY
}
