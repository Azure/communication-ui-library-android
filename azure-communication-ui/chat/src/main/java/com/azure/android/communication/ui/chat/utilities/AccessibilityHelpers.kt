package com.azure.android.communication.ui.chat.utilities

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

internal fun announceForAccessibility(
    activity: Context,
    message: String,
) {
    val manager = activity.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    if (manager.isEnabled) {
        val event = AccessibilityEvent.obtain()
        event.text.add(message)
        event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
        manager.sendAccessibilityEvent(event)
    }
}
