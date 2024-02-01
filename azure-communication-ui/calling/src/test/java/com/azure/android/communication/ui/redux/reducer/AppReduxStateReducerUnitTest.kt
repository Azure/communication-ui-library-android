// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.impl.Redux.reducer

import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.calling.redux.reducer.AudioSessionStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.CallStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.ErrorReducer
import com.azure.android.communication.ui.calling.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.LocalParticipantStateReducer
import com.azure.android.communication.ui.calling.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.ParticipantStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.PermissionStateReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.PipReducerImpl
import com.azure.android.communication.ui.calling.redux.reducer.CallDiagnosticsReducerImpl
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioSessionState
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleState
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class AppReduxStateReducerUnitTest {

    @Mock
    private lateinit var mockCallStateReducerImplementation: CallStateReducerImpl

    @Mock
    private lateinit var mockParticipantStateReducerImplementation: ParticipantStateReducerImpl

    @Mock
    private lateinit var mockDeviceStateReducer: LocalParticipantStateReducer

    @Mock
    private lateinit var mockLifecycleReducer: LifecycleReducerImpl

    @Mock
    private lateinit var mockErrorReducer: ErrorReducer

    @Mock
    private lateinit var mockPermissionStateReducerImplementation: PermissionStateReducerImpl

    @Mock
    private lateinit var mockNavigationReducerImpl: NavigationReducerImpl

    @Mock
    private lateinit var mockAudioSessionReducerImpl: AudioSessionStateReducerImpl

    @Mock
    private lateinit var pipReducer: PipReducerImpl

    @Mock
    private lateinit var mockCallDiagnosticsReducerImpl: CallDiagnosticsReducerImpl

    @Test
    fun appStateReducer_reduce_when_invoked_then_callAllReducers() {

        // arrange
        val reducer =
            AppStateReducer(
                mockCallStateReducerImplementation,
                mockParticipantStateReducerImplementation,
                mockDeviceStateReducer,
                mockPermissionStateReducerImplementation,
                mockLifecycleReducer,
                mockErrorReducer,
                mockNavigationReducerImpl,
                mockAudioSessionReducerImpl,
                pipReducer,
                mockCallDiagnosticsReducerImpl
            )
        val action = NavigationAction.CallLaunched()
        val state = AppReduxState("", false, false)
        state.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
        state.remoteParticipantState = RemoteParticipantsState(HashMap(), 0, listOf(), 0, null)
        state.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            "",
            "",
            localParticipantRole = null
        )
        state.permissionState =
            PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.NOT_ASKED)
        state.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
        state.audioSessionState = AudioSessionState(AudioFocusStatus.REJECTED)

        state.callDiagnosticsState = CallDiagnosticsState(null, null, null)

        Mockito.`when`(mockCallStateReducerImplementation.reduce(state.callState, action))
            .thenReturn(state.callState)
        Mockito.`when`(
            mockParticipantStateReducerImplementation.reduce(
                state.remoteParticipantState,
                action
            )
        )
            .thenReturn(state.remoteParticipantState)
        Mockito.`when`(
            mockDeviceStateReducer.reduce(
                state.localParticipantState,
                action
            )
        )
            .thenReturn(state.localParticipantState)
        Mockito.`when`(
            mockPermissionStateReducerImplementation.reduce(
                state.permissionState,
                action
            )
        ).thenReturn(state.permissionState)

        Mockito.`when`(
            mockLifecycleReducer.reduce(
                state.lifecycleState,
                action
            )
        ).thenReturn(state.lifecycleState)

        Mockito.`when`(
            mockErrorReducer.reduce(
                state.errorState,
                action
            )
        ).thenReturn(state.errorState)

        Mockito.`when`(
            mockNavigationReducerImpl.reduce(
                state.navigationState,
                action
            )
        ).thenReturn(state.navigationState)

        Mockito.`when`(
            mockAudioSessionReducerImpl.reduce(
                state.audioSessionState,
                action
            )
        ).thenReturn(state.audioSessionState)

        Mockito.`when`(
            pipReducer.reduce(
                state.pipState,
                action
            )
        ).thenReturn(state.pipState)

        Mockito.`when`(
            mockCallDiagnosticsReducerImpl.reduce(
                state.callDiagnosticsState,
                action
            )
        ).thenReturn(state.callDiagnosticsState)

        // act
        reducer.reduce(state, action)

        // assert
        verify(mockCallStateReducerImplementation, Mockito.times(1))
            .reduce(state.callState, action)
        verify(mockParticipantStateReducerImplementation, Mockito.times(1))
            .reduce(state.remoteParticipantState, action)
    }
}
