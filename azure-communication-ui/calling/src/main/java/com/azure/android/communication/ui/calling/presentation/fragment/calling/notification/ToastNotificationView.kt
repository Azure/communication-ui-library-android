// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ToastNotificationView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var toastNotificationView: View
    private lateinit var toastNotificationIconImageView: ImageView
    private lateinit var toastNotificationMessageTextView: TextView
    private lateinit var toastNotificationViewModel: ToastNotificationViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        toastNotificationView = findViewById(R.id.azure_communication_ui_calling_toast_notification)
        toastNotificationMessageTextView =
            findViewById(R.id.azure_communication_ui_calling_toast_notification_message)
        toastNotificationIconImageView =
            findViewById(R.id.azure_communication_ui_calling_toast_notification_icon)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        toastNotificationViewModel: ToastNotificationViewModel,
        accessibilityEnabled: Boolean
    ) {
        this.toastNotificationViewModel = toastNotificationViewModel

        viewLifecycleOwner.lifecycleScope.launch {
            toastNotificationViewModel.toastNotificationModelFlow.collect {
                if (it != null) {
                    visibility = View.VISIBLE

                    if (accessibilityEnabled) {
                        performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
                        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                        sendAccessibilityEvent(AccessibilityEvent.WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED)
                    }

                    toastNotificationIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            it.notificationIconId
                        )
                    )
                    toastNotificationMessageTextView.text = context.getString(it.notificationMessageId)
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    fun stop() {
        this.removeAllViews()
    }
}
