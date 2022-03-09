// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LobbyOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: LobbyOverlayViewModel

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: LobbyOverlayViewModel,
    ) {
        this.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayLobbyOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val lobbyOverlay: View = findViewById(R.id.azure_communication_ui_call_lobby_overlay)
            viewModel.getIsConfirmLeaveOverlayDisplayedStateFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(lobbyOverlay, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(lobbyOverlay, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }
    }
}
