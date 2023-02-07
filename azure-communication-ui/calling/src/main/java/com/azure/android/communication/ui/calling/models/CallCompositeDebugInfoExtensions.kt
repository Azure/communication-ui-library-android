// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import org.threeten.bp.OffsetDateTime

internal fun buildCallCompositeDebugInfo(callHistoryRecordList: List<CallCompositeCallHistoryRecord>) =
    CallCompositeDebugInfo(callHistoryRecordList)

internal fun buildCallHistoryRecord(callStartedOn: OffsetDateTime, callIds: List<String>): CallCompositeCallHistoryRecord {
    return CallCompositeCallHistoryRecord(callStartedOn, callIds)
}
