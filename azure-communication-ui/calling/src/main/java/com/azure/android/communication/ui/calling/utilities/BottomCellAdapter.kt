// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R

internal class BottomCellAdapter : RecyclerView.Adapter<BottomCellViewHolder>() {
    private var bottomCellItems: List<BottomCellItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomCellViewHolder {
        val bottomCellType = BottomCellItemType.values()[viewType]
        val inflater = LayoutInflater.from(parent.context)

        return when (bottomCellType) {
            BottomCellItemType.BottomMenuAction, BottomCellItemType.BottomMenuActionNoIcon -> {
                val view = inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_cell, parent, false)
                if (isAndroidTV(parent.context)) {
                    view.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) v.setBackgroundColor(v.context.resources.getColor(R.color.azure_communication_ui_calling_color_focus_tint))
                        else v.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                BottomCellActionViewHolder(view)
            }
            BottomCellItemType.BottomMenuTitle -> {
                val view = inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_title_cell, parent, false)
                BottomCellViewHolder(view)
            }
            BottomCellItemType.BottomMenuCenteredTitle -> {
                val view = inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_centered_title_cell, parent, false)
                BottomCellViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: BottomCellViewHolder, position: Int) {
        val item: BottomCellItem = bottomCellItems[position]
        // Handle title view separately
        if (item.itemType == BottomCellItemType.BottomMenuCenteredTitle ||
            item.itemType == BottomCellItemType.BottomMenuTitle ||
            item.itemType == BottomCellItemType.BottomMenuActionNoIcon
        ) {
            removeAccessibilityPositionForTitle(holder.itemView)
        } else {
            holder.itemView.accessibilityDelegate = null // Allow default behavior for languages
        }
        holder.setCellData(item)
    }

    override fun getItemCount() = bottomCellItems.size

    fun setBottomCellItems(bottomCellItems: List<BottomCellItem>) {
        this.bottomCellItems = bottomCellItems
    }

    fun enableBottomCellItem(bottomCellItemName: String) {
        for (bottomCellItem in bottomCellItems) {
            bottomCellItem.isChecked = bottomCellItem.title == bottomCellItemName
        }
        super.notifyDataSetChanged()
    }

    /**
     * Remove the position announcement from TalkBack for title items.
     */
    private fun removeAccessibilityPositionForTitle(view: View) {
        view.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfo
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                // Prevent TalkBack from announcing the position for title items
                info.collectionItemInfo = null
            }
        }
    }

    override fun getItemViewType(position: Int) = bottomCellItems[position].itemType.ordinal
}
