// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class WaitingLobbyOverlayViewModel {
    private lateinit var displayLobbyOverlayFlow: MutableStateFlow<Boolean>

    fun getDisplayLobbyOverlayFlow(): StateFlow<Boolean> = displayLobbyOverlayFlow

    fun init(
        shouldDisplayLobbyOverlay: Boolean,
    ) {
        displayLobbyOverlayFlow = MutableStateFlow(shouldDisplayLobbyOverlay)
    }

    fun update(
        shouldDisplayLobbyOverlay: Boolean,
    ) {
        displayLobbyOverlayFlow.value = shouldDisplayLobbyOverlay
    }
}
