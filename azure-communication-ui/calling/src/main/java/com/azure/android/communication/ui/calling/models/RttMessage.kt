// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import java.util.Date

internal data class RttMessage(
    val message: String,
    val senderUserRawId: String,
    val senderName: String,
    val localCreatedTime: Date,
    val isLocal: Boolean,
) {
    val isFinalized = message.endsWith("\n")
}