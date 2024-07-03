// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import java.util.Date

internal data class CaptionsDataViewModel(
    val displayName: String,
    val displayText: String,
    val speakerRawIdentifierId: String,
    val languageCode: String,
    val isFinal: Boolean,
    val timestamp: Date
)
