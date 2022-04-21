// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities

import android.graphics.drawable.Drawable
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData

internal data class BottomCellItem(
    var icon: Drawable?,
    var title: String?,
    var contentDescription: String?,
    var accessoryImage: Drawable?,
    var accessoryColor: Int?,
    var accessoryImageDescription: String?,
    var enabled: Boolean?,
    var personaData: CommunicationUIPersonaData?,
    var onClickAction: Runnable,
)
