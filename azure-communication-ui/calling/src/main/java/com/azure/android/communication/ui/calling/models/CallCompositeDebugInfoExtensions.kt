// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import android.graphics.Bitmap
import org.threeten.bp.OffsetDateTime
import java.io.File

internal fun buildCallCompositeDebugInfo(callHistoryRecordList: List<CallCompositeCallHistoryRecord>, getLogFiles: () -> List<File>, takeScreenshot: () -> Bitmap?) =
    CallCompositeDebugInfo(callHistoryRecordList, getLogFiles, takeScreenshot)

internal fun buildCallHistoryRecord(callStartedOn: OffsetDateTime, callIds: List<String>): CallCompositeCallHistoryRecord {
    return CallCompositeCallHistoryRecord(callStartedOn, callIds)
}
