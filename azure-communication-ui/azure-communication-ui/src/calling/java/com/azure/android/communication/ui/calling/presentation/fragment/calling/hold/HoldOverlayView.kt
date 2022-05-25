// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hold

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.microsoft.fluentui.widget.Button
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class HoldOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var waitingIcon: ImageView
    private lateinit var overlayTitle: TextView
    private lateinit var resumeButton: Button
    private lateinit var viewModel: HoldOverlayViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        waitingIcon =
            findViewById(R.id.azure_communication_ui_call_hold_overlay_wait_for_host_image)
        overlayTitle = findViewById(R.id.azure_communication_ui_call_hold_overlay_title)
        resumeButton = findViewById(R.id.azure_communication_ui_call_hold_resume_button)
        resumeButton.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_calling_corner_radius_rectangle_4dp_primary_background
        )
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: HoldOverlayViewModel,
    ) {
        this.viewModel = viewModel

        setupUI()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayHoldOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }

        ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat,
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                    info.isClickable = false
                }
            }
        )
    }

    private fun setupUI() {
        waitingIcon.contentDescription =
            context.getString(R.string.azure_communication_ui_calling_hold_view_text)
        overlayTitle.text =
            context.getString(R.string.azure_communication_ui_calling_hold_view_text)
        resumeButton.setOnClickListener {
            viewModel.resumeCall()
        }
    }
}
