// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.graphics.drawable.Drawable
import android.view.View
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData

internal enum class BottomCellItemType {
    BottomMenuAction,
    BottomMenuActionNoIcon,
    BottomMenuTitle,
    BottomMenuCenteredTitle
}

internal data class BottomCellItem(
    var icon: Drawable?,
    var title: String?,
    var contentDescription: String?,
    var accessoryImage: Drawable?,
    var accessoryColor: Int?,
    var accessoryImageDescription: String?,
    var isChecked: Boolean?,
    var participantViewData: CallCompositeParticipantViewData?,
    var isOnHold: Boolean?,
    val itemType: BottomCellItemType = BottomCellItemType.BottomMenuAction,
    var onClickAction: ((View) -> Unit)?,
    val isEnabled: Boolean = true,
    var showAdmitAllButton: Boolean = false,
    var showRightArrow: Boolean = false,
    var showToggleButton: Boolean = false,
    var enableToggleButton: Boolean = false,
    var isToggleButtonOn: Boolean = false,
    var subtitle: String? = null,
    var toggleButtonAction: ((View, Boolean) -> Unit)? = null,
    var admitAllButtonAction: ((View) -> Unit)? = null
)
