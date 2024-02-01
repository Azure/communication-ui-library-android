// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.impl.R
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LobbyErrorHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var lobbyErrorHeaderView: LobbyErrorHeaderView
    private lateinit var headerLayout: ConstraintLayout
    private lateinit var closeButton: ImageButton
    private lateinit var lobbyErrorHeaderViewModel: LobbyErrorHeaderViewModel
    private lateinit var errorTextView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        lobbyErrorHeaderView = this
        headerLayout = findViewById(R.id.azure_communication_ui_calling_lobby_error_header)
        closeButton = findViewById(R.id.azure_communication_ui_calling_lobby_error_close_button)
        errorTextView = findViewById(R.id.azure_communication_ui_lobby_header_error_text)
        closeButton.setOnClickListener {
            closeLobbyHeaderView()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        lobbyErrorHeaderViewModel: LobbyErrorHeaderViewModel
    ) {
        this.lobbyErrorHeaderViewModel = lobbyErrorHeaderViewModel

        viewLifecycleOwner.lifecycleScope.launch {
            this@LobbyErrorHeaderView.lobbyErrorHeaderViewModel.getDisplayLobbyErrorHeaderFlow().collect {
                lobbyErrorHeaderView.visibility = if (it) View.VISIBLE else View.GONE
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

        viewLifecycleOwner.lifecycleScope.launch {
            this@LobbyErrorHeaderView.lobbyErrorHeaderViewModel.getLobbyErrorFlow().collect {
                errorTextView.text = getLobbyErrorMessage(it)
            }
        }
    }

    private fun getLobbyErrorMessage(it: CallCompositeLobbyErrorCode?): String {
        return when (it) {
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS -> context.getString(R.string.azure_communication_ui_calling_error_lobby_disabled_by_configuration)
            CallCompositeLobbyErrorCode.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED -> context.getString(R.string.azure_communication_ui_calling_error_lobby_conversation_type_not_supported)
            CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED -> context.getString(R.string.azure_communication_ui_calling_error_lobby_meeting_role_not_allowded)
            CallCompositeLobbyErrorCode.REMOVE_PARTICIPANT_OPERATION_FAILURE -> context.getString(R.string.azure_communication_ui_calling_error_lobby_failed_to_remove_participant)
            else -> context.getString(R.string.azure_communication_ui_calling_error_lobby_unknown)
        }
    }

    private fun closeLobbyHeaderView() {
        lobbyErrorHeaderViewModel.close()
    }
}
