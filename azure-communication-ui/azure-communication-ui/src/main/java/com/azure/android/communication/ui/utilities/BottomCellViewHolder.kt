// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities

import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R

internal open class BottomCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.cell_text)
    open fun setCellData(bottomCellItem: BottomCellItem) {
        itemView.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View?,
                info: AccessibilityNodeInfo?
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                print(info)
            }

            override fun onPopulateAccessibilityEvent(host: View?, event: AccessibilityEvent?) {
                super.onPopulateAccessibilityEvent(host, event)
                print(event)
            }
        }
        title.text = bottomCellItem.title
        title.contentDescription = bottomCellItem.contentDescription
    }
}
