// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.microsoft.fluentui.widget.Button

internal open class BottomCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val icon: ImageView = itemView.findViewById(R.id.azure_communication_ui_cell_icon)
    private val title: TextView = itemView.findViewById(R.id.azure_communication_ui_cell_text)
    private val admitAllButton: Button? = itemView.findViewById(R.id.azure_communication_ui_calling_admit_all_button)

    open fun setCellData(bottomCellItem: BottomCellItem) {

        if (bottomCellItem.icon != null) {
            icon.setImageDrawable(bottomCellItem.icon)
            icon.setOnClickListener(bottomCellItem.onClickAction)
        } else {
            icon.visibility = View.GONE
        }

        title.text = bottomCellItem.title
        if (bottomCellItem.subtitle?.isNotEmpty() == true) {
            val subtitleTextView: TextView = itemView.findViewById(R.id.azure_communication_ui_calling_bottom_drawer_sub_title)
            subtitleTextView.visibility = View.VISIBLE
            subtitleTextView.text = bottomCellItem.subtitle
        }
        title.isEnabled = bottomCellItem.isEnabled
        itemView.contentDescription = bottomCellItem.contentDescription
        if (bottomCellItem.contentDescription != null)
            itemView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        else
            itemView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO

        // The for BottomMenuCenteredTitle whole item is clickable only if there is no icon, otherwise the icon only is clickable
        if (bottomCellItem.itemType != BottomCellItemType.BottomMenuCenteredTitle || bottomCellItem.icon == null) {
            itemView.setOnClickListener(bottomCellItem.onClickAction)
            itemView.isClickable = bottomCellItem.onClickAction != null && bottomCellItem.isEnabled
        }
        admitAllButton?.visibility = if (bottomCellItem.showAdmitAllButton) View.VISIBLE else View.GONE
        admitAllButton?.setOnClickListener(bottomCellItem.admitAllButtonAction)
    }
}
