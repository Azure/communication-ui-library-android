// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class InCallPrimaryNotificationView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var inCallPrimaryNotificationLayout: ConstraintLayout
    private lateinit var inCallPrimaryNotificationView: View
    private lateinit var inCallPrimaryNotificationIconImageView: ImageView
    private lateinit var inCallPrimaryNotificationMessage: TextView
    private lateinit var dismissImageButton: ImageButton
    private lateinit var inCallPrimaryNotificationViewModel: InCallPrimaryNotificationViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        inCallPrimaryNotificationLayout = this
        inCallPrimaryNotificationView = findViewById(R.id.azure_communication_ui_in_call_primary_notification)
        inCallPrimaryNotificationMessage =
            findViewById(R.id.azure_communication_ui_in_call_notification_message)
        inCallPrimaryNotificationIconImageView =
            findViewById(R.id.azure_communication_ui_in_call_notification_icon)
        dismissImageButton =
            findViewById(R.id.azure_communication_ui_in_call_notification_dismiss_button)
        dismissImageButton.setOnClickListener {
            inCallPrimaryNotificationViewModel.dismiss()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        inCallPrimaryNotificationViewModel: InCallPrimaryNotificationViewModel,
        accessibilityEnabled: Boolean
    ) {
        this.inCallPrimaryNotificationViewModel = inCallPrimaryNotificationViewModel
        setupAccessibility()
        viewLifecycleOwner.lifecycleScope.launch {
            if (accessibilityEnabled) {
                inCallPrimaryNotificationLayout.visibility = View.VISIBLE
            } else {
                inCallPrimaryNotificationViewModel.getDisplayInCallNotificationFlow().collect {
                    inCallPrimaryNotificationLayout.visibility = if (it) View.VISIBLE else View.GONE
                    // If we are on television, set the focus to the participants button
                    if (it && isAndroidTV(context)) {
                        dismissImageButton.requestFocus()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            inCallPrimaryNotificationViewModel.getInCallNotificationMessageFlow().collect {
                inCallPrimaryNotificationMessage.text = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            inCallPrimaryNotificationViewModel.getInCallNotificationIconFlow().collect {
                if (it != 0)
                    inCallPrimaryNotificationIconImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, it, null))
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            inCallPrimaryNotificationViewModel.getIsOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(inCallPrimaryNotificationView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(inCallPrimaryNotificationView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }
    }

    private fun setupAccessibility() {
        dismissImageButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_in_call_notification_dismiss_accessibility_label)
    }
}
