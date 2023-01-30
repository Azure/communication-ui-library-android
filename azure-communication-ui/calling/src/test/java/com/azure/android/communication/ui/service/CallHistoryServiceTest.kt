// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallHistoryService
import com.azure.android.communication.ui.calling.service.CallHistoryServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import org.mockito.kotlin.eq
import org.threeten.bp.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
internal class CallHistoryServiceTest : ACSBaseTestCoroutine() {

    @Test
    @ExperimentalCoroutinesApi
    fun callHistoryService_onCallStateUpdate_callsRepositoryInsert() {

        runScopedTest {
            // arrange
            val appState1 = AppReduxState("")
            appState1.callState = CallingState(CallingStatus.NONE)

            val stateFlow = MutableStateFlow<ReduxState>(appState1)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { stateFlow.value }
            }

            val callHistoryRepository = mock<CallHistoryRepository> {
                on { insert(any(), any()) } doAnswer { }
                on { getAll() } doAnswer { listOf<CallHistoryRecordData>() }
            }

            val callHistoryService: CallHistoryService = CallHistoryServiceImpl(mockAppStore, callHistoryRepository)
            val flowJob = launch {
                callHistoryService.start(coroutineScope = this)
            }

            // update state
            val appState2 = AppReduxState("")
            val callID = "callID"
            appState2.callState = CallingState(CallingStatus.CONNECTING, callID, callStartLocalDateTime = LocalDateTime.now())
            stateFlow.value = appState2

            verify(callHistoryRepository, times(1)).insert(eq(callID), any())

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callHistoryService_cleansHistory() {

        runScopedTest {
            // arrange
            val appState1 = AppReduxState("")
            appState1.callState = CallingState(CallingStatus.NONE)

            val stateFlow = MutableStateFlow<ReduxState>(appState1)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { stateFlow.value }
            }

            val historyList = mutableListOf(
                CallHistoryRecordData(1, "callId1", LocalDateTime.now().minusDays(33)),
                CallHistoryRecordData(2, "callId2", LocalDateTime.now().minusDays(32)),
                CallHistoryRecordData(3, "callId3", LocalDateTime.now().minusDays(31)),
                CallHistoryRecordData(4, "callId4", LocalDateTime.now().minusDays(30)),
            )

            val callHistoryRepository = mock<CallHistoryRepository> {
                on { remove(any()) } doAnswer { }
                on { getAll() } doAnswer { historyList }
            }

            val callHistoryService: CallHistoryService = CallHistoryServiceImpl(mockAppStore, callHistoryRepository)
            val flowJob = launch {
                callHistoryService.start(coroutineScope = this)
            }

            verify(callHistoryRepository, times(1)).remove(eq(1))
            verify(callHistoryRepository, times(1)).remove(eq(2))
            verify(callHistoryRepository, times(1)).remove(eq(3))
            verify(callHistoryRepository, times(0)).remove(eq(4))

            flowJob.cancel()
        }
    }
}
