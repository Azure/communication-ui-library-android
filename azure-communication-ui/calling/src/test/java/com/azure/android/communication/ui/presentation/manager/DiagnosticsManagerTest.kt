// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.manager.DiagnosticsManager
import com.azure.android.communication.ui.calling.presentation.manager.DiagnosticsManagerImpl
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
internal class DiagnosticsManagerTest : ACSBaseTestCoroutine() {

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

            val diagnosticsManager: DiagnosticsManager = DiagnosticsManagerImpl(mockAppStore)
            val flowJob = launch {
                diagnosticsManager.start(coroutineScope = this)
            }

            val diagnostics1 = diagnosticsManager.diagnostics
            Assert.assertNotNull(diagnostics1)
            Assert.assertNull(diagnostics1.lastKnownCallId)

            // update state
            val appState2 = AppReduxState("")
            val callID = "callID"
            appState2.callState = CallingState(CallingStatus.CONNECTING, callID)
            stateFlow.value = appState2

            val diagnostics2 = diagnosticsManager.diagnostics
            Assert.assertNotSame(diagnostics1, diagnostics2)
            Assert.assertNotNull(diagnostics2)
            Assert.assertEquals(callID, diagnostics2.lastKnownCallId)

            // redux state loosing CallID

            // update state
            val appState3 = AppReduxState("")
            appState3.callState = CallingState(CallingStatus.CONNECTING, null)
            stateFlow.value = appState3

            val diagnostics3 = diagnosticsManager.diagnostics
            Assert.assertSame(diagnostics2, diagnostics3)
            Assert.assertNotNull(diagnostics3)
            Assert.assertEquals(callID, diagnostics3.lastKnownCallId)
            flowJob.cancel()
        }
    }
}
