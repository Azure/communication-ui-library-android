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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class UpperMessageBarNotificationView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var upperMessageBarNotificationLayout: ConstraintLayout
    private lateinit var upperMessageBarNotificationView: View
    private lateinit var upperMessageBarNotificationIconImageView: ImageView
    private lateinit var upperMessageBarNotificationMessage: TextView
    private lateinit var dismissImageButton: ImageButton
    private lateinit var upperMessageBarNotificationViewModel: UpperMessageBarNotificationViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        upperMessageBarNotificationLayout = this
        upperMessageBarNotificationView = findViewById(R.id.azure_communication_ui_upper_message_bar_notification)
        upperMessageBarNotificationMessage =
            findViewById(R.id.azure_communication_ui_upper_message_bar_notification_message)
        upperMessageBarNotificationIconImageView =
            findViewById(R.id.azure_communication_ui_upper_message_bar_notification_icon)
        dismissImageButton =
            findViewById(R.id.azure_communication_ui_upper_message_bar_notification_dismiss_button)
        dismissImageButton.setOnClickListener {
            upperMessageBarNotificationViewModel.dismissNotification()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        upperMessageBarNotificationViewModel: UpperMessageBarNotificationViewModel,
        accessibilityEnabled: Boolean
    ) {
        this.upperMessageBarNotificationViewModel = upperMessageBarNotificationViewModel
        setupAccessibility()

        upperMessageBarNotificationLayout.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            upperMessageBarNotificationViewModel.getUpperMessageBarNotificationModelFlow().collect {
                if (!it.isEmpty()) {
                    upperMessageBarNotificationIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it.notificationIconId
                        )
                    )
                    upperMessageBarNotificationMessage.text = context.getString(it.notificationMessageId)
                }
            }
        }
    }

    private fun setupAccessibility() {
        dismissImageButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_notification_dismiss_accessibility_label)
    }
}
