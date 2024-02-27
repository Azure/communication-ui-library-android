// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.redux.state.CallingStatus

internal class JoinCallButtonHolderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var setupJoinCallButton: Button
    private lateinit var setupJoinCallButtonText: AppCompatTextView

    private lateinit var progressBar: ProgressBar
    private lateinit var joiningCallText: AppCompatTextView

    private lateinit var viewModel: JoinCallButtonHolderViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupJoinCallButton = findViewById(R.id.azure_communication_ui_setup_join_call_button)
        setupJoinCallButtonText =
            findViewById(R.id.azure_communication_ui_setup_start_call_button_text)
        progressBar = findViewById(R.id.azure_communication_ui_setup_start_call_progress_bar)
        joiningCallText = findViewById(R.id.azure_communication_ui_setup_start_call_joining_text)
        setupJoinCallButton.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_calling_corner_radius_rectangle_4dp_primary_background
        )
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: JoinCallButtonHolderViewModel
    ) {
        this.viewModel = viewModel
        if (viewModel.getCallType() == CallType.ONE_TO_N_CALL_OUTGOING) {
            setupJoinCallButtonText.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_start_call)
            joiningCallText.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_starting_call)
        } else {
            setupJoinCallButtonText.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_join_call)
            joiningCallText.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_connecting_call)
        }

        setupJoinCallButton.setOnClickListener {
            viewModel.launchCallScreen()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getJoinCallButtonEnabledFlow().collect {
                onJoinCallEnabledChanged(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCallingStatusStateFlow().collect {
                if (it == CallingStatus.RINGING) {
                    // joiningCallText.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_ringing_call)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisableJoinCallButtonFlow().collect { onDisableJoinCallButtonChanged(it) }
        }
    }

    private fun onJoinCallEnabledChanged(isEnabled: Boolean) {
        setupJoinCallButton.isEnabled = isEnabled
        setupJoinCallButtonText.isEnabled = isEnabled
    }

    private fun onDisableJoinCallButtonChanged(isBlocked: Boolean) {
        if (isBlocked) {
            setupJoinCallButton.visibility = GONE
            setupJoinCallButtonText.visibility = GONE
            progressBar.visibility = VISIBLE
            joiningCallText.visibility = VISIBLE

            joiningCallText.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        } else {
            setupJoinCallButton.visibility = VISIBLE
            setupJoinCallButtonText.visibility = VISIBLE
            progressBar.visibility = GONE
            joiningCallText.visibility = GONE
        }
    }
}
