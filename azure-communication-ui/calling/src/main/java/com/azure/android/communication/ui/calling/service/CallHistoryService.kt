// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

internal interface CallHistoryService {
    fun start(coroutineScope: CoroutineScope)
}

internal class CallHistoryServiceImpl(
    private val store: Store<ReduxState>,
    private val callHistoryRepository: CallHistoryRepository,
) : CallHistoryService {
    private val callIdStateFlow = MutableStateFlow<String?>(null)

    override fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                callIdStateFlow.value = it.callState.callId
            }
        }

        coroutineScope.launch {
            callIdStateFlow.collect { callId ->
                val callStartDateTime = store.getCurrentState().callState.callStartLocalDateTime
                if (callId != null && callStartDateTime != null) {
                    callHistoryRepository.insertCallHistoryRecord(callId, callStartDateTime)
                }
            }
        }

        coroutineScope.launch {
            cleanOldRecords()
        }
    }

    private fun cleanOldRecords() {
        val thresholdDate = LocalDateTime.now().minusDays(31)
        callHistoryRepository.getAllCallHistoryRecords().forEach {
            if (thresholdDate > it.date) {
                callHistoryRepository.removeCallHistoryRecord(it.id)
            }
        }
    }
}
