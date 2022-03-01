// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.header

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class InfoHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var floatingHeader: ConstraintLayout
    private lateinit var participantNumberText: TextView
    private lateinit var displayParticipantsImageButton: ImageButton
    private lateinit var infoHeaderViewModel: InfoHeaderViewModel
    private lateinit var displayParticipantListCallback: () -> Unit

    override fun onFinishInflate() {
        super.onFinishInflate()
        floatingHeader = this
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
        accessibilityEnabled: Boolean
    ) {
        this.infoHeaderViewModel = infoHeaderViewModel
        this.displayParticipantListCallback = displayParticipantList

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
                when (it) {
                    0 -> participantNumberText.setText(R.string.azure_communication_ui_calling_view_info_header_waiting_for_others_to_join)
                    1 -> participantNumberText.setText(R.string.azure_communication_ui_calling_view_info_header_call_with_1_person)
                    else ->
                        participantNumberText.text =
                            resources.getString(
                                R.string.azure_communication_ui_calling_view_info_header_call_with_n_people,
                                it
                            )
                }
            }
        }
    }
}
