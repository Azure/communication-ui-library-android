// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data.model

import org.threeten.bp.LocalDateTime

internal data class CallHistoryRecord(
    val id: Int,
    val callId: String,
    val date: LocalDateTime,
)
