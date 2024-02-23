// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
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
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
internal class CallHistoryServiceTest : ACSBaseTestCoroutine() {

    @Test
    @ExperimentalCoroutinesApi
    fun callHistoryService_onCallStateUpdate_callsRepositoryInsert() {

        runScopedTest {
            // arrange
            val appState1 = AppReduxState("", false, false)
            appState1.callState = CallingState(CallingStatus.NONE)

            val stateFlow = MutableStateFlow<ReduxState>(appState1)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { stateFlow.value }
            }

            val callHistoryRepository = mock<CallHistoryRepository> {
                onBlocking { insert(any(), any()) } doAnswer { }
            }

            val callHistoryService: CallHistoryService = CallHistoryServiceImpl(mockAppStore, callHistoryRepository)
            val flowJob = launch {
                callHistoryService.start(coroutineScope = this)
            }

            // update state
            val appState2 = AppReduxState("", false, false)
            val callID = "callID"
            appState2.callState = CallingState(
                CallingStatus.CONNECTING,
                callID,
                callStartDateTime = OffsetDateTime.now()
            )
            stateFlow.value = appState2

            verify(callHistoryRepository, times(1)).insert(eq(callID), any())

            flowJob.cancel()
        }
    }
}
