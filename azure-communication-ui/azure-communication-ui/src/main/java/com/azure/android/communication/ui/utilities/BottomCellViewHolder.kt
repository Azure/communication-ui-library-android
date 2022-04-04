// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.microsoft.fluentui.persona.AvatarView

internal class BottomCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.cell_icon)
    private val avatarView: AvatarView =
        itemView.findViewById(R.id.azure_communication_ui_participant_list_avatar)
    private val title: TextView = itemView.findViewById(R.id.cell_text)
    private val accessoryImage: ImageView = itemView.findViewById(R.id.cell_check_mark)
    private var onClickAction: Runnable? = null

    init {
        itemView.setOnClickListener {
            onClickAction?.run()
        }
    }

    fun setCellData(bottomCellItem: BottomCellItem) {
        title.text = bottomCellItem.title
        if (bottomCellItem.icon == null) {
            ViewCompat.setAccessibilityDelegate(
                itemView,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                        info.isClickable = false
                    }
                }
            )
            imageView.visibility = View.GONE
            avatarView.visibility = View.VISIBLE
            avatarView.name = bottomCellItem.title ?: ""
            title.contentDescription = bottomCellItem.contentDescription
        } else {
            imageView.setImageDrawable(bottomCellItem.icon)
            avatarView.visibility = View.GONE
        }
        accessoryImage.setImageDrawable(bottomCellItem.accessoryImage)
        if (bottomCellItem.accessoryColor != null) {
            accessoryImage.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    bottomCellItem.accessoryColor!!
                )
            )
        }
        accessoryImage.contentDescription = bottomCellItem.accessoryImageDescription
        accessoryImage.visibility = if (bottomCellItem.enabled) View.VISIBLE else View.INVISIBLE
        onClickAction = bottomCellItem.onClickAction
    }
}
