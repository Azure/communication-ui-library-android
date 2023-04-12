// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.presentation.manager.CallManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CallManagerUnitTests {
    @Test
    fun lifecycleManager_callStateConnected_then_callEndRequestedTriggered() {
        // Arrange
        val state = AppReduxState("", false, false)
        state.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn state
            on { dispatch(any()) } doAnswer { }
        }
        val mockErrorHandler = mock<ErrorHandler> {}

        val callManager = CallManager(mockErrorHandler, mockAppStore)

        // Act
        callManager.hangup()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallingAction.CallEndRequested
            }
        )
    }

    @Test
    fun lifecycleManager_callStateNotConnected_then_errorEventTriggered() {
        // Arrange
        val state = AppReduxState("", false, false)
        state.callState = CallingState(CallingStatus.CONNECTING, OperationStatus.SKIP_SETUP_SCREEN)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn state
        }
        val mockErrorHandler = mock<ErrorHandler> {
            on { notifyErrorEvent(any()) } doAnswer { }
        }

        val callManager = CallManager(mockErrorHandler, mockAppStore)

        // Act
        callManager.hangup()

        // Assert
        verify(mockErrorHandler, times(1)).notifyErrorEvent(
            argThat { eventArg ->
                eventArg is CallCompositeErrorEvent && eventArg.errorCode == CallCompositeErrorCode.CALL_END_FAILED
            }
        )
    }
}
