// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.state.AppReduxState

internal class AppStateReducer(
    private val callStateReducer: CallStateReducer,
    private val participantStateReducer: ParticipantStateReducer,
    private val deviceStateReducer: LocalParticipantStateReducer,
    private val permissionStateReducer: PermissionStateReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
) :
    Reducer<AppReduxState> {
    override fun reduce(state: AppReduxState, action: Action): AppReduxState {

        val appState = AppReduxState(state.localParticipantState.displayName)

        appState.callState = callStateReducer.reduce(
            state.callState,
            action
        )

        appState.remoteParticipantState = participantStateReducer.reduce(
            state.remoteParticipantState,
            action
        )

        appState.localParticipantState = deviceStateReducer.reduce(
            state.localParticipantState,
            action
        )

        appState.permissionState = permissionStateReducer.reduce(state.permissionState, action)
        appState.lifecycleState = lifecycleReducer.reduce(state.lifecycleState, action)
        appState.errorState = errorReducer.reduce(state.errorState, action)
        appState.navigationState = navigationReducer.reduce(state.navigationState, action)

        return appState
    }
}
