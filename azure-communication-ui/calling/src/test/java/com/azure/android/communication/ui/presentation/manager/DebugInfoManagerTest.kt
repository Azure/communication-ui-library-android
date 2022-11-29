// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class DebugInfoManagerTest : ACSBaseTestCoroutine() {

    @Test
    @ExperimentalCoroutinesApi
    fun diagnosticsManager_getCallId_returns_callId() {

        runScopedTest {
            // arrange
            val appState1 = AppReduxState("")

            val stateFlow = MutableStateFlow<ReduxState>(appState1)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
            }

            val debugInfoManager: DebugInfoManager = DebugInfoManagerImpl(mockAppStore)
            val flowJob = launch {
                debugInfoManager.start(coroutineScope = this)
            }

            val diagnostics1 = debugInfoManager.debugInfo
            Assert.assertNotNull(diagnostics1)
            Assert.assertNull(diagnostics1.lastCallId)

            // update state
            val appState2 = AppReduxState("")
            val callID = "callID"
            appState2.callState = CallingState(CallingStatus.CONNECTING, callID)
            stateFlow.value = appState2

            val diagnostics2 = debugInfoManager.debugInfo
            Assert.assertNotSame(diagnostics1, diagnostics2)
            Assert.assertNotNull(diagnostics2)
            Assert.assertEquals(callID, diagnostics2.lastCallId)

            // redux state loosing CallID

            // update state
            val appState3 = AppReduxState("")
            appState3.callState = CallingState(CallingStatus.CONNECTING, null)
            stateFlow.value = appState3

            val diagnostics3 = debugInfoManager.debugInfo
            Assert.assertSame(diagnostics2, diagnostics3)
            Assert.assertNotNull(diagnostics3)
            Assert.assertEquals(callID, diagnostics3.lastCallId)
            flowJob.cancel()
        }
    }
}
