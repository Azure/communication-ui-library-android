// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
internal class DebugInfoManagerTest : ACSBaseTestCoroutine() {

    @Test
    @ExperimentalCoroutinesApi
    fun diagnosticsManager_getCallId_returns_callId() {

        runScopedTest {
            // arrange
            val historyList = mutableListOf(
                CallHistoryRecordData(1, "callId1", OffsetDateTime.now().minusDays(6)),
                CallHistoryRecordData(2, "callId2", OffsetDateTime.now().minusDays(4)),
                CallHistoryRecordData(3, "callId3", OffsetDateTime.now().minusDays(3)),
                CallHistoryRecordData(4, "callId4", OffsetDateTime.now().minusDays(1)),
            )

            val callHistoryRepository = mock<CallHistoryRepository> {
                onBlocking { getAll() } doAnswer { historyList }
            }

            val debugInfoManager: DebugInfoManager = DebugInfoManagerImpl(callHistoryRepository)

            val debugIndo = debugInfoManager.getDebugInfo()
            Assert.assertNotNull(debugIndo)
            Assert.assertEquals(historyList.count(), debugIndo.callHistoryRecords.count())
            Assert.assertEquals(historyList.last().id, 4)

            val diagnostics1 = debugInfoManager.debugInfo
            Assert.assertNotNull(diagnostics1)
            Assert.assertNull(diagnostics1.lastCallId)

            // update state
            val appState2 = AppReduxState("")
            val callID = "callID"
            appState2.callState = CallingState(CallingStatus.CONNECTING, OperationStatus.NONE, callID)
            stateFlow.value = appState2

            val diagnostics2 = debugInfoManager.debugInfo
            Assert.assertNotSame(diagnostics1, diagnostics2)
            Assert.assertNotNull(diagnostics2)
            Assert.assertEquals(callID, diagnostics2.lastCallId)

            // redux state loosing CallID

            // update state
            val appState3 = AppReduxState("")
            appState3.callState = CallingState(CallingStatus.CONNECTING, OperationStatus.NONE, null)
            stateFlow.value = appState3

            val diagnostics3 = debugInfoManager.debugInfo
            Assert.assertSame(diagnostics2, diagnostics3)
            Assert.assertNotNull(diagnostics3)
            Assert.assertEquals(callID, diagnostics3.lastCallId)
            flowJob.cancel()
        }
    }
}
