// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ConfirmLeaveOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var confirmLeaveOverlayViewModel: ConfirmLeaveOverlayViewModel
    private lateinit var confirmLeaveCallButton: Button
    private lateinit var cancelLeaveCallButton: Button
    private lateinit var leaveOverlay: LinearLayout

    override fun onFinishInflate() {
        super.onFinishInflate()
        leaveOverlay = this
        confirmLeaveCallButton =
            findViewById(R.id.azure_communication_ui_call_leave_confirm)
        cancelLeaveCallButton =
            findViewById(R.id.azure_communication_ui_call_leave_cancel)
        confirmLeaveCallButton.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp_primary_background
        )
        subscribeClickListener()
    }

    fun stop() {
        confirmLeaveOverlayViewModel.cancel()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        confirmLeaveOverlayViewModel: ConfirmLeaveOverlayViewModel,
    ) {
        this.confirmLeaveOverlayViewModel = confirmLeaveOverlayViewModel

        setupUi()
        viewLifecycleOwner.lifecycleScope.launch {
            confirmLeaveOverlayViewModel.getShouldDisplayConfirmLeaveOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }
    }

    private fun setupUi() {
        confirmLeaveCallButton.text = context.getString(R.string.azure_communication_ui_calling_view_overlay_leave_call)

        cancelLeaveCallButton.text = context.getString(R.string.azure_communication_ui_calling_view_overlay_cancel)
    }

    private fun subscribeClickListener() {
        confirmLeaveCallButton.setOnClickListener {
            confirmLeaveOverlayViewModel.confirm()
        }

        cancelLeaveCallButton.setOnClickListener {
            confirmLeaveOverlayViewModel.cancel()
        }

        leaveOverlay.setOnClickListener {
            confirmLeaveOverlayViewModel.cancel()
        }
    }
}
