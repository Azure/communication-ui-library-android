// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CaptionsInfoView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var floatingHeader: ConstraintLayout
    private lateinit var captionsInfoView: View
    private lateinit var captionsText: TextView
    private lateinit var viewModel: CaptionsInfoViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        floatingHeader = this
        captionsInfoView = findViewById(R.id.azure_communication_ui_calling_captions_info_view)
        captionsText = findViewById(R.id.azure_communication_ui_calling_caption_info_text)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: CaptionsInfoViewModel
    ) {
        this.viewModel = viewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayCaptionsInfoViewFlow().collect {
                if (it) {
                    floatingHeader.visibility = View.VISIBLE
                } else {
                    floatingHeader.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCaptionsDataSharedFlow()?.collect {
                var displayName = it.speakerName
                if (displayName.isEmpty()) {
                    displayName = context.getString(R.string.azure_communication_ui_calling_captions_no_display_name)
                }

                var displayText = it.captionText
                if (displayText.isNullOrEmpty()) {
                    displayText = it.spokenText
                }

                if (displayText.isNotEmpty()) {
                    val spannableString = SpannableString("$displayName: $displayText")

                    val displayNameColor = ContextCompat.getColor(floatingHeader.context, R.color.azure_communication_ui_calling_color_focus_highlight)
                    val startIndex = 0
                    val endIndex = displayName.length + 1
                    spannableString.setSpan(ForegroundColorSpan(displayNameColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    captionsText.text = spannableString
                    viewModel.showCaptionsData()
                }
            }
        }
    }
}
