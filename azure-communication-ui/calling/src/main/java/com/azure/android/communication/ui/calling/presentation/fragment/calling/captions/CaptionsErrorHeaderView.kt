// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsErrors
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CaptionsErrorHeaderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var captionsErrorHeaderView: CaptionsErrorHeaderView
    private lateinit var headerLayout: ConstraintLayout
    private lateinit var closeButton: ImageButton
    private lateinit var captionsErrorHeaderViewModel: CaptionsErrorHeaderViewModel
    private lateinit var errorTextView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        captionsErrorHeaderView = this
        headerLayout = findViewById(R.id.azure_communication_ui_calling_captions_error_header)
        closeButton = findViewById(R.id.azure_communication_ui_calling_captions_error_close_button)
        errorTextView = findViewById(R.id.azure_communication_ui_captions_header_error_text)
        closeButton.setOnClickListener {
            closeCaptionsHeaderView()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        captionsErrorHeaderViewModel: CaptionsErrorHeaderViewModel
    ) {
        this.captionsErrorHeaderViewModel = captionsErrorHeaderViewModel

        viewLifecycleOwner.lifecycleScope.launch {
            this@CaptionsErrorHeaderView.captionsErrorHeaderViewModel.getDisplayCaptionsErrorHeaderFlow().collect {
                captionsErrorHeaderView.visibility = if (it) View.VISIBLE else View.GONE
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
            this@CaptionsErrorHeaderView.captionsErrorHeaderViewModel.getCaptionsErrorFlow().collect {
                errorTextView.text = getLobbyErrorMessage(it)
            }
        }
    }

    private fun getLobbyErrorMessage(it: CallCompositeCaptionsErrors?): String {
        return when (it) {
            CallCompositeCaptionsErrors.CAPTIONS_NOT_ACTIVE -> context.getString(R.string.azure_communication_ui_calling_error_captions_not_active)
            CallCompositeCaptionsErrors.GET_CAPTIONS_FAILED_CALL_STATE_NOT_CONNECTED -> context.getString(R.string.azure_communication_ui_calling_error_captions_failed_call_state_not_connected)
            CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_START -> context.getString(R.string.azure_communication_ui_calling_error_captions_failed_to_start)
            CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_STOP -> context.getString(R.string.azure_communication_ui_calling_error_captions_failed_to_stop)
            CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE -> context.getString(R.string.azure_communication_ui_calling_error_captions_failed_to_set_spoken_language)
            CallCompositeCaptionsErrors.FAILED_TO_SET_CAPTION_LANGUAGE -> context.getString(R.string.azure_communication_ui_calling_error_failed_to_set_caption_language)
            CallCompositeCaptionsErrors.CAPTIONS_POLICY_DISABLED -> context.getString(R.string.azure_communication_ui_calling_error_captions_policy_disabled)
            CallCompositeCaptionsErrors.CAPTIONS_DISABLED_BY_CONFIGURATIONS -> context.getString(R.string.azure_communication_ui_calling_error_captions_disabled_by_configurations)
            CallCompositeCaptionsErrors.CAPTIONS_SET_SPOKEN_LANGUAGE_DISABLED -> context.getString(R.string.azure_communication_ui_calling_error_captions_set_spoken_language_disabled)
            CallCompositeCaptionsErrors.SET_CAPTION_LANGUAGE_DISABLED -> context.getString(R.string.azure_communication_ui_calling_error_set_caption_language_disabled)
            CallCompositeCaptionsErrors.SET_CAPTION_LANGUAGE_TEAMS_PREMIUM_LICENSE_NEEDED -> context.getString(R.string.azure_communication_ui_calling_error_set_caption_language_teams_premium_license_needed)
            CallCompositeCaptionsErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED -> context.getString(R.string.azure_communication_ui_calling_error_captions_requested_language_not_supported)
            else -> context.getString(R.string.azure_communication_ui_calling_error_captions_unknown)
        }
    }

    private fun closeCaptionsHeaderView() {
        captionsErrorHeaderViewModel.close()
    }
}
