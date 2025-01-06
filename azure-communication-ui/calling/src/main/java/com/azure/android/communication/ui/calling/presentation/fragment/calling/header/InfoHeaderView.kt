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
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import com.azure.android.communication.ui.calling.utilities.launchAll
import com.microsoft.fluentui.util.activity
import kotlinx.coroutines.flow.collect

internal class InfoHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var floatingHeader: ConstraintLayout
    private lateinit var headerView: View
    private lateinit var participantNumberText: TextView
    private lateinit var subtitleText: TextView
    /* <CALL_START_TIME> */
    private lateinit var callDurationText: TextView
    /* </CALL_START_TIME> */
    private lateinit var displayParticipantsImageButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var customButton1: ImageButton
    private lateinit var customButton2: ImageButton
    private lateinit var infoHeaderViewModel: InfoHeaderViewModel
    private lateinit var displayParticipantListCallback: () -> Unit

    override fun onFinishInflate() {
        super.onFinishInflate()
        floatingHeader = this
        headerView = findViewById(R.id.azure_communication_ui_call_floating_header)
        participantNumberText =
            findViewById(R.id.azure_communication_ui_call_participant_number_text)
        subtitleText = findViewById(R.id.azure_communication_ui_call_header_subtitle)
        /* <CALL_START_TIME> */
        callDurationText = findViewById(R.id.azure_communication_ui_call_header_duration)
        /* </CALL_START_TIME> */
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
        customButton1 = findViewById(R.id.azure_communication_ui_call_header_custom_button_1)
        customButton2 = findViewById(R.id.azure_communication_ui_call_header_custom_button_2)
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
        viewLifecycleOwner.lifecycleScope.launchAll(
            {
                if (accessibilityEnabled) {
                    floatingHeader.visibility = View.VISIBLE
                } else {
                    infoHeaderViewModel.getDisplayFloatingHeaderFlow().collect {
                        floatingHeader.visibility = if (it) View.VISIBLE else View.GONE
                        // If we are on television, set the focus to the participants button
                        if (it && isAndroidTV(context)) {
                            displayParticipantsImageButton.requestFocus()
                        }
                    }
                }
            },
            {
                infoHeaderViewModel.getTitleStateFlow().collect {
                    if (it.isNullOrEmpty()) {
                        return@collect
                    }
                    participantNumberText.text = it
                }
            },
            {
                infoHeaderViewModel.getSubtitleStateFlow().collect {
                    if (it.isNullOrEmpty()) {
                        subtitleText.visibility = View.GONE
                        return@collect
                    }
                    subtitleText.text = it
                    subtitleText.visibility = View.VISIBLE
                }
            },
            {
                infoHeaderViewModel.getNumberOfParticipantsFlow().collect {
                    if (!infoHeaderViewModel.getTitleStateFlow().value.isNullOrEmpty()) {
                        return@collect
                    }

                    participantNumberText.text = when (it) {
                        0 -> context.getString(R.string.azure_communication_ui_calling_view_info_header_waiting_for_others_to_join)

                        1 -> context.getString(R.string.azure_communication_ui_calling_view_info_header_call_with_1_person)

                        else -> resources.getString(
                            R.string.azure_communication_ui_calling_view_info_header_call_with_n_people,
                            it
                        )
                    }
                }
            },
            {
                infoHeaderViewModel.getIsOverlayDisplayedFlow().collect {
                    if (it) {
                        ViewCompat.setImportantForAccessibility(
                            headerView,
                            ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                        )
                    } else {
                        ViewCompat.setImportantForAccessibility(
                            headerView,
                            ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
                        )
                    }
                }
            },
            {
                infoHeaderViewModel.getCustomButton1StateFlow().collect { button ->
                    updateCustomButton(button, customButton1)
                }
            },
            {
                infoHeaderViewModel.getCustomButton2StateFlow().collect { button ->
                    updateCustomButton(button, customButton2)
                }
            },
            /* <CALL_START_TIME> */
            {
                infoHeaderViewModel.getDisplayCallDurationFlow().collect {
                    callDurationText.isVisible = it
                }
            },
            {
                infoHeaderViewModel.getCallDurationFlow().collect {
                    callDurationText.text = it
                }
            }
            /* </CALL_START_TIME> */
        )
    }

    private fun updateCustomButton(customButtonEntry: InfoHeaderViewModel.CustomButtonEntry?, customButton: ImageButton) {
        customButton.visibility = if (customButtonEntry?.isVisible == true) View.VISIBLE else View.GONE
        customButtonEntry?.let {
            customButton.isEnabled = customButtonEntry.isEnabled
            customButton.setImageResource(customButtonEntry.icon)
            customButton.setOnClickListener {
                infoHeaderViewModel.onCustomButtonClicked(context, customButtonEntry.id)
            }
        }
    }

    private fun setupAccessibility() {
        displayParticipantsImageButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_participant_list_open_accessibility_label)
        backButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_go_back)
    }
}
