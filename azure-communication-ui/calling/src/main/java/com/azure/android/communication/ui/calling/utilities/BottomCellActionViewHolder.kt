// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.azure.android.communication.ui.calling.implementation.R
import com.microsoft.fluentui.persona.AvatarView

internal class BottomCellActionViewHolder(itemView: View) : BottomCellViewHolder(itemView) {
    private val icon: ImageView = itemView.findViewById(R.id.azure_communication_ui_cell_icon)
    private val avatarView: AvatarView =
        itemView.findViewById(R.id.azure_communication_ui_participant_list_avatar)
    private val avatarViewForImage: AvatarView =
        itemView.findViewById(R.id.azure_communication_ui_participant_list_avatar_image)
    private val accessoryImageView: ImageView = itemView.findViewById(R.id.azure_communication_ui_cell_check_mark)
    private val additionalText: TextView = itemView.findViewById(R.id.azure_communication_ui_cell_additional_text)
    private val showMoreImageView: ImageView = itemView.findViewById(R.id.azure_communication_ui_calling_bottom_drawer_cell_arrow_next)
    private val switchCompat: SwitchCompat = itemView.findViewById(R.id.azure_communication_ui_calling_bottom_drawer_toggle_button)

    override fun setCellData(bottomCellItem: BottomCellItem) {
        super.setCellData(bottomCellItem)
        itemView.isEnabled = bottomCellItem.isEnabled
        itemView.alpha = if (bottomCellItem.isEnabled) 1.0f else 0.5f

        if (bottomCellItem.itemType == BottomCellItemType.BottomMenuActionNoIcon) {
            accessoryImageView.contentDescription = bottomCellItem.accessoryImageDescription
            accessoryImageView.visibility =
                if (bottomCellItem.isChecked == true) View.VISIBLE else View.INVISIBLE
            avatarView.visibility = View.GONE
            avatarViewForImage.visibility = View.GONE
            icon.visibility = View.GONE
            showMoreImageView.visibility = View.GONE
            additionalText.visibility = View.GONE
            switchCompat.visibility = View.GONE
            return
        }

        if (bottomCellItem.itemType == BottomCellItemType.BottomMenuTitle) {
            // Exit with early return because setting the title is the only thing we need to do
            return
        }
        // force bitmap update be setting resource to 0
//        avatarView.setImageResource(0)
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
                }
            )

            if (bottomCellItem.title == itemView.context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed)) {
                avatarView.name = ""
            } else {
                avatarView.name = bottomCellItem.title ?: ""
            }

            // Hide icon and avatarViewForImage. Show avatar for initials.
            icon.visibility = View.GONE
            avatarView.visibility = View.VISIBLE
            avatarViewForImage.visibility = View.GONE
            bottomCellItem.participantViewData?.let { participantViewData ->
                participantViewData.avatarBitmap?.let {
                    // Use avatarViewForImage to display image.
                    // Due to avatarView.setImageResource(0) is not removing the previous image,
                    // if ViewHolder is re-used for another item, the previous image will be shown.
                    avatarViewForImage.setImageBitmap(it)
                    avatarViewForImage.adjustViewBounds = true
                    avatarViewForImage.scaleType = participantViewData.scaleType
                    avatarViewForImage.visibility = View.VISIBLE
                    avatarView.visibility = View.GONE
                }
            }
        } else {
            icon.setImageDrawable(bottomCellItem.icon)
            avatarView.visibility = View.GONE
            avatarViewForImage.visibility = View.GONE
        }
        if (bottomCellItem.accessoryImage != null) {
            accessoryImageView.setImageDrawable(bottomCellItem.accessoryImage)
        }
        if (bottomCellItem.accessoryColor != null) {
            accessoryImageView.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    bottomCellItem.accessoryColor!!
                )
            )
        }
        accessoryImageView.contentDescription = bottomCellItem.accessoryImageDescription
        accessoryImageView.visibility =
            if (isAccessoryImageViewable(bottomCellItem)) View.VISIBLE else View.INVISIBLE
        additionalText.visibility = if (bottomCellItem.isOnHold == true) View.VISIBLE else View.INVISIBLE
        showMoreImageView.visibility = if (bottomCellItem.showRightArrow) View.VISIBLE else View.INVISIBLE
        switchCompat.visibility = if (bottomCellItem.showToggleButton) View.VISIBLE else View.GONE
        switchCompat.isEnabled = bottomCellItem.isEnabled
        switchCompat.isChecked = bottomCellItem.isToggleButtonOn
        switchCompat.setOnCheckedChangeListener { buttonView, isChecked ->
            bottomCellItem.toggleButtonAction?.invoke(buttonView, isChecked)
        }
    }

    private fun isAccessoryImageViewable(bottomCellItem: BottomCellItem): Boolean {
        val muteDescription = itemView.rootView.context
            .getString(R.string.azure_communication_ui_calling_view_participant_list_muted_accessibility_label)
        val unMutedDescription = itemView.rootView.context
            .getString(R.string.azure_communication_ui_calling_view_participant_list_unmuted_accessibility_label)

        return (
            bottomCellItem.isOnHold == false && (
                bottomCellItem.isChecked == true || bottomCellItem.accessoryImageDescription == muteDescription ||
                    bottomCellItem.accessoryImageDescription == unMutedDescription
                )
            )
    }
}
