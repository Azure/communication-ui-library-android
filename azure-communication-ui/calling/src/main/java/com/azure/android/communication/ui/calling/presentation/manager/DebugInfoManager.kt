// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo
import com.azure.android.communication.ui.calling.models.buildCallCompositeDebugInfo
import com.azure.android.communication.ui.calling.models.buildCallHistoryRecord

internal interface DebugInfoManager {
    val debugInfo: CallCompositeDebugInfo
}

internal class DebugInfoManagerImpl(
    private val callHistoryRepository: CallHistoryRepository,
) : DebugInfoManager {

    override val debugInfo: CallCompositeDebugInfo
        get() {
            return buildCallCompositeDebugInfo(getCallHistory())
        }

    private fun getCallHistory(): List<CallCompositeCallHistoryRecord> {
        return callHistoryRepository.getAllCallHistoryRecords()
            .groupBy {
                it.date
            }
            .map { mapped ->
                buildCallHistoryRecord(mapped.key, mapped.value.map { it.callId })
            }
            .sortedBy {
                it.callStartedOn
            }
    }
}
