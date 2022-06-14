// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import org.reduxkotlin.Reducer

internal class AppStateReducer(
    private val callStateReducer: Reducer<CallingState>,
    private val participantStateReducer: ParticipantStateReducer,
    private val deviceStateReducer: LocalParticipantStateReducer,
    private val permissionStateReducer: PermissionStateReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
    private val audioSessionReducer: AudioSessionReducer,
) :
    Reducer<AppReduxState> {
    override fun invoke(state: AppReduxState, action: Any): AppReduxState {

        val appState = AppReduxState(state.localParticipantState.displayName)

        appState.callState = callStateReducer.invoke(
            state.callState,
            action
        )

        appState.remoteParticipantState = participantStateReducer.invoke(
            state.remoteParticipantState,
            action
        )

        appState.localParticipantState = deviceStateReducer.invoke(
            state.localParticipantState,
            action
        )

        appState.permissionState = permissionStateReducer.invoke(state.permissionState, action)
        appState.lifecycleState = lifecycleReducer.invoke(state.lifecycleState, action)
        appState.errorState = errorReducer.invoke(state.errorState, action)
        appState.navigationState = navigationReducer.invoke(state.navigationState, action)
        appState.audioSessionState = audioSessionReducer.invoke(state.audioSessionState, action)
        return appState
    }

}
