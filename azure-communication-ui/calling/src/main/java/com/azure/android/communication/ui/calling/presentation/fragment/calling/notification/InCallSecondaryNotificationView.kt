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

internal class InCallSecondaryNotificationView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var inCallNotificationLayout: ConstraintLayout
    private lateinit var inCallNotificationView: View
    private lateinit var inCallNotificationIconImageView: ImageView
    private lateinit var inCallNotificationMessage: TextView
    private lateinit var dismissImageButton: ImageButton
    private lateinit var inCallSecondaryNotificationViewModel: InCallSecondaryNotificationViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        inCallNotificationLayout = this
        inCallNotificationView = findViewById(R.id.azure_communication_ui_in_call_secondary_notification)
        inCallNotificationMessage =
            findViewById(R.id.azure_communication_ui_in_call_notification_message)
        inCallNotificationIconImageView =
            findViewById(R.id.azure_communication_ui_in_call_notification_icon)
        dismissImageButton =
            findViewById(R.id.azure_communication_ui_in_call_notification_dismiss_button)
        dismissImageButton.setOnClickListener {
            inCallSecondaryNotificationViewModel.dismiss()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        inCallSecondaryNotificationViewModel: InCallSecondaryNotificationViewModel,
        accessibilityEnabled: Boolean
    ) {
        this.inCallSecondaryNotificationViewModel = inCallSecondaryNotificationViewModel
        setupAccessibility()
        viewLifecycleOwner.lifecycleScope.launch {
            if (accessibilityEnabled) {
                inCallNotificationLayout.visibility = View.VISIBLE
            } else {
                inCallSecondaryNotificationViewModel.getDisplayInCallNotificationFlow().collect {
                    inCallNotificationLayout.visibility = if (it) View.VISIBLE else View.GONE
                    // If we are on television, set the focus to the participants button
                    if (it && isAndroidTV(context)) {
                        dismissImageButton.requestFocus()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            inCallSecondaryNotificationViewModel.getInCallNotificationMessageFlow().collect {
                inCallNotificationMessage.text = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            inCallSecondaryNotificationViewModel.getInCallNotificationIconFlow().collect {
                if (it != 0)
                    inCallNotificationIconImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, it, null))
            }
        }

        /*viewLifecycleOwner.lifecycleScope.launch {
            inCallSecondaryNotificationViewModel.getIsOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(inCallNotificationView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(inCallNotificationView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }*/
    }

    private fun setupAccessibility() {
        dismissImageButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_in_call_notification_dismiss_accessibility_label)
    }
}
