// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.utilities.BottomMenuCellActionViewHolder

internal class BottomMenuCellAdapter : RecyclerView.Adapter<BottomMenuCellTitleViewHolder>() {
    private var bottomCellItems: List<BottomCellItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomMenuCellTitleViewHolder {
        val bottomCellType = BottomMenuCellItemType.values()[viewType]
        val inflater = LayoutInflater.from(parent.context)
        return when (bottomCellType) {
            BottomMenuCellItemType.BottomMenuAction -> {
                val view = inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_cell, parent, false)
                BottomMenuCellActionViewHolder(view)
            }
            BottomMenuCellItemType.BottomMenuTitle -> {
                val view = inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_title_cell, parent, false)
                BottomMenuCellTitleViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: BottomMenuCellTitleViewHolder, position: Int) {
        val item: BottomCellItem = bottomCellItems[position]
        holder.setCellData(item)
    }

    override fun getItemCount() = bottomCellItems.size

    fun setBottomCellItems(bottomCellItems: List<BottomCellItem>) {
        this.bottomCellItems = bottomCellItems
    }

    fun enableBottomCellItem(bottomCellItemName: String) {
        for (bottomCellItem in bottomCellItems) {
            bottomCellItem.enabled = bottomCellItem.title == bottomCellItemName
        }
        super.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = bottomCellItems[position].itemType.ordinal
}
