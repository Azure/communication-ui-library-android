// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R

internal open class BottomCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.cell_text)
    private var onClickAction: Runnable? = null
    init {
        itemView.setOnClickListener {
            onClickAction?.run()
        }
    }
    open fun setCellData(bottomCellItem: BottomCellItem) {
        title.text = bottomCellItem.title
        title.contentDescription = bottomCellItem.contentDescription
        onClickAction = bottomCellItem.onClickAction
    }
}
