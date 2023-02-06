// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.threeten.bp.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
internal class DebugInfoManagerTest : ACSBaseTestCoroutine() {

    @Test
    @ExperimentalCoroutinesApi
    fun diagnosticsManager_getCallId_returns_callId() {

        runScopedTest {
            // arrange
            val historyList = mutableListOf(
                CallHistoryRecordData(1, "callId1", LocalDateTime.now().minusDays(6)),
                CallHistoryRecordData(2, "callId2", LocalDateTime.now().minusDays(4)),
                CallHistoryRecordData(3, "callId3", LocalDateTime.now().minusDays(3)),
                CallHistoryRecordData(4, "callId4", LocalDateTime.now().minusDays(1)),
            )

            val callHistoryRepository = mock<CallHistoryRepository> {
                onBlocking { getAll() } doAnswer { historyList }
            }

            val debugInfoManager: DebugInfoManager = DebugInfoManagerImpl(callHistoryRepository)

            val debugIndo = debugInfoManager.getDebugInfo()
            Assert.assertNotNull(debugIndo)
            Assert.assertEquals(historyList.count(), debugIndo.callHistoryRecords.count())
            Assert.assertEquals(historyList.last().id, 4)
        }
    }
}
