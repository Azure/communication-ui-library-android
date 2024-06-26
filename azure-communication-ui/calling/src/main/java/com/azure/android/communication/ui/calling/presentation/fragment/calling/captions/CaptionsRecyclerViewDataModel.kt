// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.graphics.Bitmap

internal data class CaptionsRecyclerViewDataModel(
    val displayName: String,
    val displayText: String,
    val avatarBitmap: Bitmap?
)
