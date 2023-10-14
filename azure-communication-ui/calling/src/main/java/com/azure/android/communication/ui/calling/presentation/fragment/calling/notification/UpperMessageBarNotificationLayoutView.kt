// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class UpperMessageBarNotificationLayoutView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var upperMessageBarNotificationLayout: LinearLayout

    private lateinit var upperMessageBarNotificationLayoutViewModel: UpperMessageBarNotificationLayoutViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        upperMessageBarNotificationLayout = this
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        upperMessageBarNotificationLayoutViewModel: UpperMessageBarNotificationLayoutViewModel,
        accessibilityEnabled: Boolean
    ) {
        this.upperMessageBarNotificationLayoutViewModel = upperMessageBarNotificationLayoutViewModel
        upperMessageBarNotificationLayout.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow()?.collect() {
                if (!it.isEmpty()) {
                    val upperMessageBarNotificationView: UpperMessageBarNotificationView = inflate(
                        context,
                        R.layout.azure_communication_ui_calling_upper_message_bar_notification,
                        null
                    ) as UpperMessageBarNotificationView
                    val upperMessageBarNotificationViewModel = UpperMessageBarNotificationViewModel()
                    upperMessageBarNotificationViewModel.init (it) { upperMessageBarNotificationLayoutViewModel.dismissNotification(it.mediaCallDiagnostic) }
                    upperMessageBarNotificationView.start(
                        viewLifecycleOwner,
                        upperMessageBarNotificationViewModel,
                        accessibilityEnabled
                    )
                    it.notificationView = upperMessageBarNotificationView

                    val layoutParams = LinearLayout.LayoutParams(upperMessageBarNotificationLayout.layoutParams)
                    layoutParams.bottomMargin = (8 * context.resources.displayMetrics.density).toInt()
                    upperMessageBarNotificationLayout.addView(upperMessageBarNotificationView, layoutParams)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            upperMessageBarNotificationLayoutViewModel.getDismissUpperMessageBarNotificationFlow()?.collect() {
                if (!it.isEmpty() && it.notificationView != null) {
                    upperMessageBarNotificationLayout.removeView(it.notificationView)
                }
            }
        }
    }

    fun stop() {
        this.removeAllViews()
    }
}
