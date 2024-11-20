// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.graphics.Bitmap
import java.util.Date

internal enum class CaptionsRttType {
    CAPTIONS,
    RTT,
}

internal data class CaptionsRttRecord(
    val avatarBitmap: Bitmap?,
    val displayName: String,
    val displayText: String,
    val speakerRawId: String,
    val languageCode: String?,
    val isFinal: Boolean,
    val timestamp: Date,
    val type: CaptionsRttType,
    val isLocal: Boolean? = null,
)
