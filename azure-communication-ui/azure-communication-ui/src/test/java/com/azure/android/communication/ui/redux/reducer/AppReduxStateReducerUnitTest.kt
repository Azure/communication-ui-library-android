// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.NavigationAction
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CallingState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.LifecycleState
import com.azure.android.communication.ui.redux.state.LifecycleStatus
import com.azure.android.communication.ui.redux.state.LocalUserState
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
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
                mockNavigationReducerImpl
            )
        val action = NavigationAction.CallLaunched()
        val state = AppReduxState("")
        state.callState = CallingState(CallingStatus.CONNECTED)
        state.remoteParticipantState = RemoteParticipantsState(HashMap(), 0)
        state.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            "",
            ""
        )
        state.permissionState =
            PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.NOT_ASKED)
        state.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

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

        // act
        reducer.reduce(state, action)

        // assert
        verify(mockCallStateReducerImplementation, Mockito.times(1))
            .reduce(state.callState, action)
        verify(mockParticipantStateReducerImplementation, Mockito.times(1))
            .reduce(state.remoteParticipantState, action)
    }
}
