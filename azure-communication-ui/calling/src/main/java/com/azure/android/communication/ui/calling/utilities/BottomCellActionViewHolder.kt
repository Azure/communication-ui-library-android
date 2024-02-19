// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.azure.android.communication.ui.calling.implementation.R
import com.microsoft.fluentui.persona.AvatarView

internal class BottomCellActionViewHolder(itemView: View) : BottomCellViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.azure_communication_ui_cell_icon)
    private val avatarView: AvatarView =
        itemView.findViewById(R.id.azure_communication_ui_participant_list_avatar)
    private val accessoryImageView: ImageView = itemView.findViewById(R.id.azure_communication_ui_cell_check_mark)
    private val additionalText: TextView = itemView.findViewById(R.id.azure_communication_ui_cell_additional_text)

    override fun setCellData(bottomCellItem: BottomCellItem) {
        super.setCellData(bottomCellItem)
        if (bottomCellItem.itemType == BottomCellItemType.BottomMenuTitle) {
            // Exit with early return because setting the title is the only thing we need to do
            return
        }
        // force bitmap update be setting resource to 0
        avatarView.setImageResource(0)
        if (bottomCellItem.icon == null) {
            ViewCompat.setAccessibilityDelegate(
                itemView,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfoCompat,
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                        info.isClickable = false
                    }
                },
            )
            imageView.visibility = View.GONE
            avatarView.visibility = View.VISIBLE

            if (bottomCellItem.title == itemView.context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed)) {
                avatarView.name = ""
            } else {
                avatarView.name = bottomCellItem.title ?: ""
            }

            bottomCellItem.participantViewData?.let { participantViewData ->
                participantViewData.avatarBitmap?.let {
                    avatarView.avatarImageBitmap = it
                    avatarView.adjustViewBounds = true
                    avatarView.scaleType = participantViewData.scaleType
                }
            }
        } else {
            imageView.setImageDrawable(bottomCellItem.icon)
            avatarView.visibility = View.GONE
        }
        if (bottomCellItem.accessoryImage != null) {
            accessoryImageView.setImageDrawable(bottomCellItem.accessoryImage)
        }
        if (bottomCellItem.accessoryColor != null) {
            accessoryImageView.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    bottomCellItem.accessoryColor!!,
                ),
            )
        }
        accessoryImageView.contentDescription = bottomCellItem.accessoryImageDescription
        accessoryImageView.visibility =
            if (isAccessoryImageViewable(bottomCellItem)) View.VISIBLE else View.INVISIBLE
        additionalText.visibility = if (bottomCellItem.isOnHold == true) View.VISIBLE else View.INVISIBLE
    }

    private fun isAccessoryImageViewable(bottomCellItem: BottomCellItem): Boolean {
        val muteDescription =
            itemView.rootView.context
                .getString(R.string.azure_communication_ui_calling_view_participant_list_muted_accessibility_label)
        val unMutedDescription =
            itemView.rootView.context
                .getString(R.string.azure_communication_ui_calling_view_participant_list_unmuted_accessibility_label)

        return (
            bottomCellItem.isOnHold == false && (
                bottomCellItem.isChecked == true || bottomCellItem.accessoryImageDescription == muteDescription ||
                    bottomCellItem.accessoryImageDescription == unMutedDescription
            )
        )
    }
}
