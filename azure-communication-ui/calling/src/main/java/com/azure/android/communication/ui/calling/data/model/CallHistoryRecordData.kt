// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data.model

import org.threeten.bp.OffsetDateTime

internal data class CallHistoryRecordData(
    val id: Int,
    val callId: String,
    val callStartedOn: OffsetDateTime,
)
