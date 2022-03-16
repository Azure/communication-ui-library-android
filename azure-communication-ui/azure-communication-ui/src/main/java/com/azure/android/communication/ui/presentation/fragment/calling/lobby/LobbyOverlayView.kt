// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LobbyOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var waitingIcon: ImageView
    private lateinit var overlayTitle: TextView
    private lateinit var overlayInfo: TextView
    private lateinit var viewModel: LobbyOverlayViewModel


    override fun onFinishInflate() {
        super.onFinishInflate()
        waitingIcon =
            findViewById(R.id.azure_communication_ui_call_call_lobby_overlay_wait_for_host_image)
        overlayTitle = findViewById(R.id.azure_communication_ui_call_lobby_overlay_title)
        overlayInfo = findViewById(R.id.azure_communication_ui_call_lobby_overlay_info)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: LobbyOverlayViewModel,
    ) {
        this.viewModel = viewModel

        setUpUi()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayLobbyOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }
    }

    private fun setUpUi() {
        waitingIcon.contentDescription = viewModel.getApplicationLocalizationProvider()
            .getLocalizedString(
                context,
                R.string.azure_communication_ui_lobby_view_text_waiting_for_host
            )

        overlayTitle.text = viewModel.getApplicationLocalizationProvider()
            .getLocalizedString(
                context,
                R.string.azure_communication_ui_lobby_view_text_waiting_for_host
            )

        overlayInfo.text = viewModel.getApplicationLocalizationProvider()
            .getLocalizedString(
                context,
                R.string.azure_communication_ui_lobby_view_text_waiting_details
            )
    }
}
