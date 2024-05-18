// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.microsoft.fluentui.widget.Button

internal open class BottomCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.azure_communication_ui_cell_text)
    private val admitAllButton: Button? = itemView.findViewById(R.id.azure_communication_ui_calling_admit_all_button)

    open fun setCellData(bottomCellItem: BottomCellItem) {
        title.text = bottomCellItem.title
        title.isEnabled = bottomCellItem.isEnabled
        itemView.contentDescription = bottomCellItem.contentDescription
        if (bottomCellItem.contentDescription != null)
            itemView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        else
            itemView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        itemView.setOnClickListener(bottomCellItem.onClickAction)
        itemView.isClickable = bottomCellItem.onClickAction != null && bottomCellItem.isEnabled

        admitAllButton?.visibility = View.GONE
        admitAllButton?.visibility = if (bottomCellItem.showAdmitAllButton) View.VISIBLE else View.GONE
        admitAllButton?.setOnClickListener(bottomCellItem.admitAllButtonAction)
    }
}
