// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.graphics.drawable.Drawable
import android.view.View
import com.azure.android.communication.ui.calling.models.ParticipantViewData

internal enum class BottomCellItemType {
    BottomMenuAction,
    BottomMenuTitle
}

internal data class BottomCellItem(
    var icon: Drawable?,
    var title: String?,
    var contentDescription: String?,
    var accessoryImage: Drawable?,
    var accessoryColor: Int?,
    var accessoryImageDescription: String?,
    var enabled: Boolean?,
    var participantViewData: ParticipantViewData?,
    var isOnHold: Boolean,
    val itemType: BottomCellItemType = BottomCellItemType.BottomMenuAction,
    var onClickAction: ((View) -> Unit)?,
)
