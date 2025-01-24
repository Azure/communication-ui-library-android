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
    BottomMenuCenteredTitle,
}

internal data class BottomCellItem(
    var icon: Drawable? = null,
    var iconContentDescription: String? = null,
    var iconOnClickAction: ((View) -> Unit)? = null,
    var title: String? = null,
    var contentDescription: String? = null,
    var accessoryImage: Drawable? = null,
    var accessoryColor: Int? = null,
    var accessoryImageDescription: String? = null,
    var isChecked: Boolean? = null,
    var participantViewData: CallCompositeParticipantViewData? = null,
    var isOnHold: Boolean? = null,
    val itemType: BottomCellItemType = BottomCellItemType.BottomMenuAction,
    var onClickAction: ((View) -> Unit)? = null,
    val isEnabled: Boolean = true,
    var showAdmitAllButton: Boolean = false,
    var showRightArrow: Boolean = false,
    var showToggleButton: Boolean = false,
    var isToggleButtonOn: Boolean = false,
    var subtitle: String? = null,
    var toggleButtonAction: ((View, Boolean) -> Unit)? = null,
    var admitAllButtonAction: ((View) -> Unit)? = null,
    val showTopDivider: Boolean = false,
)
