// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.InitialCallJoinState
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import org.junit.Assert
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
internal class LeaveConfirmViewModelUnitTest {

    @Test
    fun leaveConfirmViewModel_confirm_then_dispatchEndCall() {

        val appState = AppReduxState("", false, false, false)
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore)

        leaveConfirmViewModel.confirm()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallingAction.CallEndRequested
            }
        )
    }

    @Test
    fun leaveConfirmViewModel_confirm_onRinging_then_dispatchEndCall() {

        val appState = AppReduxState("", false, false, false)
        appState.callState = CallingState(CallingStatus.RINGING)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore)

        leaveConfirmViewModel.confirm()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallingAction.CallEndRequested
            }
        )
    }

    @Test
    fun leaveConfirmViewModel_confirm_onConnecting_then_dispatchEndCall() {

        val appState = AppReduxState("", false, false, false)
        appState.callState = CallingState(CallingStatus.RINGING)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore)

        leaveConfirmViewModel.confirm()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallingAction.CallEndRequested
            }
        )
    }

    @Test
    fun leaveConfirmViewModel_confirm_then_dispatchNavigationExit() {

        val appState = AppReduxState("", false, false)
        appState.callState = CallingState(CallingStatus.DISCONNECTED)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE,
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "name",
            localParticipantRole = null,
            initialCallJoinState = InitialCallJoinState(skipSetupScreen = true)
        )

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore)

        leaveConfirmViewModel.confirm()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is NavigationAction.Exit
            }
        )
    }

    @Test
    fun leaveConfirmViewModel_cancel_then_isLeaveConfirmDisplayed_updated() {
        val mockAppStore = mock<AppStore<ReduxState>> {}

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore)

        leaveConfirmViewModel.cancel()

        Assert.assertEquals(
            false,
            leaveConfirmViewModel.getShouldDisplayLeaveConfirmFlow().value
        )
    }

    @Test
    fun leaveConfirmViewModel_requestExitConfirmation_then_isLeaveConfirmDisplayed_updated() {
        val mockAppStore = mock<AppStore<ReduxState>> { }

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore)

        leaveConfirmViewModel.requestExitConfirmation()

        Assert.assertEquals(
            true,
            leaveConfirmViewModel.getShouldDisplayLeaveConfirmFlow().value
        )
    }
}
