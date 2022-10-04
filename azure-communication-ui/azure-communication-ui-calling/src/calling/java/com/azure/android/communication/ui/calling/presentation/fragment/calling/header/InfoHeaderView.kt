// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CustomButtonConfigurationProxy
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonType
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonViewData
import kotlinx.coroutines.launch

internal class InfoHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var floatingHeader: ConstraintLayout
    private lateinit var headerView: View
    private lateinit var participantNumberText: TextView
    private lateinit var displayParticipantsImageButton: ImageButton
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
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        infoHeaderViewModel: InfoHeaderViewModel,
        displayParticipantList: () -> Unit,
        accessibilityEnabled: Boolean,
        customButtons: Collection<CallCompositeCustomButtonViewData>?
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
            infoHeaderViewModel.getIsLobbyOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(headerView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(headerView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }

        setupCustomButtons(customButtons)
    }

    private fun setupCustomButtons(customButtons: Collection<CallCompositeCustomButtonViewData>?) {
        if (customButtons == null) {
            return
        }

        val buttonsContainer = findViewById<LinearLayout>(R.id.azure_communication_ui_call_banner_custom_button_container)
        buttonsContainer.removeAllViews()

        // TODO: extract to separate control with image button and badge. Temp using ImageButton directly.
        customButtons
            .filter { it.customButtonType == CallCompositeCustomButtonType.CALL_SCREEN_INFO_HEADER }
            .forEach { customButton ->
                val imageButton = ImageButton(buttonsContainer.context)
                imageButton.setOnClickListener {
                    customButton.onClickEventHandler?.handle(null)
                }
                imageButton.contentDescription = customButton.description
                imageButton.setImageResource(customButton.imageResourceId)
                imageButton.background.setTint(ContextCompat.getColor(context, R.color.azure_communication_ui_calling_color_button_background_transparent))

                val proxy = CustomButtonConfigurationProxy(customButton, imageButton)
                proxy.setOnFieldUpdatedListener(this::onFieldUpdatedListener)

                buttonsContainer.addView(imageButton)
            }
    }

    private fun onFieldUpdatedListener(customButton: CallCompositeCustomButtonViewData, imageButton: ImageButton) {
        imageButton.contentDescription = customButton.description
        imageButton.setImageResource(customButton.imageResourceId)
    }

    private fun setupAccessibility() {
        displayParticipantsImageButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_participant_list_open_accessibility_label)
    }
}
