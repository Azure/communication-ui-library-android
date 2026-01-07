// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.os.Build
import android.view.View
import androidx.core.graphics.Insets
import com.azure.android.communication.ui.calling.implementation.R
import java.lang.ref.WeakReference

/**
 * Singleton to store window insets values from the activity for use in dialogs.
 * Dialogs often don't receive window insets properly, so we store them from the activity.
 */
internal object WindowInsetsManager {
    private val listeners = mutableListOf<WeakReference<()-> Unit>>()

    var inserts: Insets? = null
        set(value) {
            field = value
            notifyListeners()
        }

    val top: Int get() { return inserts?.top ?: 0 }
    val bottom: Int get() { return inserts?.bottom ?: 0 }
    val left: Int get() { return inserts?.left ?: 0 }
    val right: Int get() { return inserts?.right ?: 0 }

    fun updatePaddings(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val bottomPadding =
                view.resources.getDimension(R.dimen.azure_communication_ui_calling_sheet_bottom_inset)
                    .toInt()
            val rightPadding =
                view.resources.getDimension(R.dimen.azure_communication_ui_calling_sheet_right_inset)
                    .toInt()
            view.setPadding(
                0, // Left padding is managed by FluentUI DrawerDialog
                0, // controls never reach to the top area
                WindowInsetsManager.right + rightPadding,
                WindowInsetsManager.bottom + bottomPadding
            )
        }
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(WeakReference(listener))
    }

    fun removeListener(listener: () -> Unit) {
        listeners.removeAll { it.get() == listener || it.get() == null }
    }

    private fun notifyListeners() {
        listeners.forEach { it.get()?.invoke() }
        listeners.removeAll { it.get() == null }
    }
}
