// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo
import com.azure.android.communication.ui.calling.models.buildCallCompositeDebugInfo
import com.azure.android.communication.ui.calling.models.buildCallHistoryRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

internal interface DebugInfoManager {
    fun getDebugInfo(): CallCompositeDebugInfo
}

internal class DebugInfoManagerImpl(
    private val callHistoryRepository: CallHistoryRepository,
    private val getLogFiles: () -> List<File>,
) : DebugInfoManager {

    override fun getDebugInfo(): CallCompositeDebugInfo {
        val callHistory = runBlocking {
            withContext(Dispatchers.IO) { getCallHistory() }
        }
        return buildCallCompositeDebugInfo(callHistory, getLogFiles)
    }

    private suspend fun getCallHistory(): List<CallCompositeCallHistoryRecord> {
        return callHistoryRepository.getAll()
            .groupBy {
                it.callStartedOn
            }
            .map { mapped ->
                buildCallHistoryRecord(mapped.key, mapped.value.map { it.callId })
            }
            .sortedBy {
                it.callStartedOn
            }
    }
}
