// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.azure.android.communication.ui.R

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
        ) as Drawable
        subscribeClickListener()
    }

    fun stop() {
        confirmLeaveOverlayViewModel.cancel()
    }

    fun start(confirmLeaveOverlayViewModel: ConfirmLeaveOverlayViewModel) {
        this.confirmLeaveOverlayViewModel = confirmLeaveOverlayViewModel
        setLeaveOverlay()
    }

    fun showHangupOverlay() {
        confirmLeaveOverlayViewModel.setConfirmLeaveOverlayState(View.VISIBLE)
        setLeaveOverlay()
    }

    private fun subscribeClickListener() {
        confirmLeaveCallButton.setOnClickListener {
            confirmLeaveOverlayViewModel.confirm()
        }

        cancelLeaveCallButton.setOnClickListener {
            confirmLeaveOverlayViewModel.cancel()
            setLeaveOverlay()
        }

        leaveOverlay.setOnClickListener {
            confirmLeaveOverlayViewModel.cancel()
            setLeaveOverlay()
        }
    }

    private fun setLeaveOverlay() {
        leaveOverlay.visibility = confirmLeaveOverlayViewModel.getConfirmLeaveOverlayState()
    }
}
