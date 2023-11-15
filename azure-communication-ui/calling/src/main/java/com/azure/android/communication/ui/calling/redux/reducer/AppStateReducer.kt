// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class AppStateReducer(
    private val callStateReducer: CallStateReducer,
    private val participantStateReducer: ParticipantStateReducer,
    private val deviceStateReducer: LocalParticipantStateReducer,
    private val permissionStateReducer: PermissionStateReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
    private val audioSessionReducer: AudioSessionReducer,
    private val callDiagnosticsReducer: CallDiagnosticsReducer
) :
    Reducer<ReduxState> {
    override fun reduce(state: ReduxState, action: Action): ReduxState {
        return state.copy(
            callState = callStateReducer.reduce(
                state.callState,
                action
            ),
            remoteParticipantState = participantStateReducer.reduce(
                state.remoteParticipantState,
                action
            ),
            localParticipantState = deviceStateReducer.reduce(
                state.localParticipantState,
                action
            ),
            permissionState = permissionStateReducer.reduce(state.permissionState, action),
            lifecycleState = lifecycleReducer.reduce(state.lifecycleState, action),
            errorState = errorReducer.reduce(state.errorState, action),
            navigationState = navigationReducer.reduce(state.navigationState, action),
            audioSessionState = audioSessionReducer.reduce(state.audioSessionState, action),
            callDiagnosticsState = callDiagnosticsReducer.reduce(state.callDiagnosticsState, action)
        )
    }
}
