// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.middleware

import com.azure.android.communication.ui.logger.DefaultLogger
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.ErrorAction
import com.azure.android.communication.ui.redux.action.LifecycleAction
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.redux.state.ReduxState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CallingMiddlewareUnitTest {

    @Mock
    private lateinit var mockLogger: DefaultLogger

    @Test
    fun callingMiddleware_invoke_when_invokedWithAnyAction_then_invokeNext() {
        // arrange
        val actionToDispatch = CallingAction.CallStartRequested()
        var nextReceivedAction: CallingAction? = null

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {}

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(action) {
                nextReceivedAction = action as CallingAction
            }
        )(actionToDispatch)

        // assert
        Assert.assertEquals(
            actionToDispatch,
            nextReceivedAction
        )
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithEnterBackground_then_invokeEnterBackground() {
        // arrange
        val actionToDispatch = LifecycleAction.EnterBackgroundTriggered()

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { enterBackground(any()) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).enterBackground(any())
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithEnterForeground_then_invokeEnterForeground() {
        // arrange
        val actionToDispatch = LifecycleAction.EnterForegroundTriggered()

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { enterForeground(any()) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).enterForeground(any())
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithRequestCameraOff_then_invokeRequestCameraOff() {
        // arrange
        val actionToDispatch = LocalParticipantAction.CameraOffTriggered()

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { turnCameraOff(any()) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).turnCameraOff(any())
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithRequestCameraOn_then_invokeRequestCameraOn() {
        // arrange
        val actionToDispatch = LocalParticipantAction.CameraOnTriggered()

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { turnCameraOn(any()) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).turnCameraOn(any())
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithEndCall_then_invokeEndCall() {
        // arrange
        val actionToDispatch = CallingAction.CallEndRequested()

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { endCall(mockAppStore) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).endCall(mockAppStore)
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithStartCall_then_invokeStartCall() {
        // arrange
        val actionToDispatch = CallingAction.CallStartRequested()

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { startCall(any()) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).startCall(any())
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithEmergencyExit_then_invokeExit() {
        // arrange
        val actionToDispatch = ErrorAction.EmergencyExit()

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { exit(mockAppStore) } doAnswer {}
        }

        val callingMiddlewareImplementation =
            CallingMiddlewareImpl(
                mockCallingMiddlewareActionHandler,
                mockLogger
            )

        // act
        callingMiddlewareImplementation.invoke(mockAppStore)(
            fun(_) {
            }
        )(actionToDispatch)

        // assert
        verify(mockCallingMiddlewareActionHandler, times(1)).exit(mockAppStore)
    }
}
