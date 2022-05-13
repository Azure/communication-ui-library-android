// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R

internal open class BottomCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.cell_text)
    open fun setCellData(bottomCellItem: BottomCellItem) {
        title.text = bottomCellItem.title
        itemView.contentDescription = bottomCellItem.contentDescription
        if (bottomCellItem.contentDescription != null)
            itemView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        else
            itemView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        itemView.setOnClickListener(bottomCellItem.onClickAction)
        itemView.isClickable = bottomCellItem.onClickAction != null
    }
}
