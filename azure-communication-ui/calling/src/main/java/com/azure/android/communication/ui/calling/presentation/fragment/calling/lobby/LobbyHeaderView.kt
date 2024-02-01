// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.impl.R
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import com.microsoft.fluentui.widget.Button
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LobbyHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var lobbyHeaderView: LobbyHeaderView
    private lateinit var headerLayout: ConstraintLayout
    private lateinit var closeButton: ImageButton
    private lateinit var lobbyHeaderViewModel: LobbyHeaderViewModel
    private lateinit var displayParticipantListCallback: () -> Unit
    private lateinit var participantListButton: Button

    override fun onFinishInflate() {
        super.onFinishInflate()
        lobbyHeaderView = this
        headerLayout = findViewById(R.id.azure_communication_ui_calling_lobby_header)
        closeButton = findViewById(R.id.azure_communication_ui_calling_lobby_close_button)
        closeButton.setOnClickListener {
            closeLobbyHeaderView()
        }
        participantListButton = findViewById(R.id.azure_communication_ui_calling_lobby_open_list_button)
        participantListButton.setOnClickListener {
            displayParticipantListCallback()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        lobbyHeaderViewModel: LobbyHeaderViewModel,
        displayParticipantList: () -> Unit
    ) {
        this.lobbyHeaderViewModel = lobbyHeaderViewModel
        this.displayParticipantListCallback = displayParticipantList
        setupAccessibility()

        viewLifecycleOwner.lifecycleScope.launch {
            lobbyHeaderViewModel.getDisplayLobbyHeaderFlow().collect {
                lobbyHeaderView.visibility = if (it) View.VISIBLE else View.GONE
                // If we are on television, set the focus to the participants button
                if (it && isAndroidTV(context)) {
                    headerLayout.requestFocus()
                }

                if (it) {
                    ViewCompat.setImportantForAccessibility(headerLayout, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(headerLayout, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }
    }

    private fun setupAccessibility() {
        participantListButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_lobby_header_accessibility_label)
    }

    private fun closeLobbyHeaderView() {
        lobbyHeaderViewModel.close()
    }
}
