// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import com.microsoft.fluentui.util.activity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class InfoHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var floatingHeader: ConstraintLayout
    private lateinit var headerView: View
    private lateinit var participantNumberText: TextView
    private lateinit var displayParticipantsImageButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var infoHeaderViewModel: InfoHeaderViewModel
    private lateinit var displayParticipantListCallback: () -> Unit

    override fun onFinishInflate() {
        super.onFinishInflate()
        floatingHeader = this
        headerView = findViewById(R.id.azure_communication_ui_call_floating_header)
        participantNumberText =
            findViewById(R.id.azure_communication_ui_call_participant_number_text)
        displayParticipantsImageButton =
            findViewById(R.id.azure_communication_ui_call_bottom_drawer_button)
        displayParticipantsImageButton.setOnClickListener {
            displayParticipantListCallback()
        }
        backButton = findViewById(R.id.azure_communication_ui_call_header_back_button)

        backButton.setOnClickListener {
            if (infoHeaderViewModel.multitaskingEnabled) {
                (context.activity as? MultitaskingCallCompositeActivity)?.hide()
            } else {
                infoHeaderViewModel.requestCallEnd()
            }
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        infoHeaderViewModel: InfoHeaderViewModel,
        displayParticipantList: () -> Unit,
        accessibilityEnabled: Boolean
    ) {
        this.infoHeaderViewModel = infoHeaderViewModel
        this.displayParticipantListCallback = displayParticipantList
        setupAccessibility()
        viewLifecycleOwner.lifecycleScope.launch {
            if (accessibilityEnabled) {
                floatingHeader.visibility = View.VISIBLE
            } else {
                infoHeaderViewModel.getDisplayFloatingHeaderFlow().collect {
                    floatingHeader.visibility = if (it) View.VISIBLE else View.INVISIBLE
                    // If we are on television, set the focus to the participants button
                    if (it && isAndroidTV(context)) {
                        displayParticipantsImageButton.requestFocus()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            infoHeaderViewModel.getNumberOfParticipantsFlow().collect {
                participantNumberText.text = when (it) {
                    0 -> context.getString(R.string.azure_communication_ui_calling_view_info_header_waiting_for_others_to_join)

                    1 -> context.getString(R.string.azure_communication_ui_calling_view_info_header_call_with_1_person)

                    else -> resources.getString(
                        R.string.azure_communication_ui_calling_view_info_header_call_with_n_people,
                        it
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            infoHeaderViewModel.getIsOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(headerView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(headerView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }
    }

    private fun setupAccessibility() {
        displayParticipantsImageButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_participant_list_open_accessibility_label)
        backButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_go_back)
    }
}
