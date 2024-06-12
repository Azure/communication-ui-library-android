// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.AudioSessionAction
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
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

    @Test
    fun callingMiddleware_invoke_when_invokedWithAdmitAll_then_invokeAdmitAll() {
        // arrange
        val actionToDispatch = ParticipantAction.AdmitAll()

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { admitAll(mockAppStore) } doAnswer {}
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
        verify(mockCallingMiddlewareActionHandler, times(1)).admitAll(mockAppStore)
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithAdmit_then_invokeAdmit() {
        // arrange
        val actionToDispatch = ParticipantAction.Admit("id")

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { admit(actionToDispatch.userIdentifier, mockAppStore) } doAnswer {}
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
        verify(mockCallingMiddlewareActionHandler, times(1)).admit(actionToDispatch.userIdentifier, mockAppStore)
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithDecline_then_invokeDecline() {
        // arrange
        val actionToDispatch = ParticipantAction.Reject("id")

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { reject(actionToDispatch.userIdentifier, mockAppStore) } doAnswer {}
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
        verify(mockCallingMiddlewareActionHandler, times(1)).reject(actionToDispatch.userIdentifier, mockAppStore)
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithAudioDeviceChangeRequested_then_invokeAudioDeviceChangeRequested() {
        // arrange
        val actionToDispatch = LocalParticipantAction.AudioDeviceChangeRequested(
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED
        )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { onAudioDeviceChangeRequested(actionToDispatch.requestedAudioDevice, mockAppStore) } doAnswer {}
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
        verify(mockCallingMiddlewareActionHandler, times(1)).onAudioDeviceChangeRequested(actionToDispatch.requestedAudioDevice, mockAppStore)
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithAudioDeviceChangeSucceeded_then_invokeAudioDeviceChangeSucceeded() {
        // arrange
        val actionToDispatch = LocalParticipantAction.AudioDeviceChangeSucceeded(
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED
        )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { onAudioDeviceChangeSucceeded(actionToDispatch.selectedAudioDevice, mockAppStore) } doAnswer {}
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
        verify(mockCallingMiddlewareActionHandler, times(1)).onAudioDeviceChangeSucceeded(actionToDispatch.selectedAudioDevice, mockAppStore)
    }

    @Test
    fun callingMiddleware_invoke_when_invokedWithAudioFocusRequesting_then_invokeAudioFocusRequesting() {
        // arrange
        val actionToDispatch = AudioSessionAction.AudioFocusRequesting()

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val mockCallingMiddlewareActionHandler = mock<CallingMiddlewareActionHandler> {
            on { onAudioFocusRequesting(mockAppStore) } doAnswer {}
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
        verify(mockCallingMiddlewareActionHandler, times(1)).onAudioFocusRequesting(mockAppStore)
    }
}
