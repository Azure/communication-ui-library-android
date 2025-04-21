// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.state.AppReduxState

internal class AppStateReducer(
    private val callStateReducer: CallStateReducer,
    private val participantStateReducer: ParticipantStateReducer,
    private val localParticipantReducer: LocalParticipantStateReducer,
    private val permissionStateReducer: PermissionStateReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
    private val audioSessionReducer: AudioSessionReducer,
    private val pipReducer: PipReducer,
    private val callDiagnosticsReducer: CallDiagnosticsReducer,
    private val toastNotificationReducer: ToastNotificationReducer,
    private val captionsReducer: CaptionsReducer,
    private val callScreenInformationHeaderReducer: CallScreenInformationHeaderReducer,
    private val buttonViewDataReducer: ButtonViewDataReducer,
    private val rttReducer: RttReducer,
    private val deviceConfigurationReducer: DeviceConfigurationReducer,
) : Reducer<AppReduxState> {

    override fun reduce(state: AppReduxState, action: Action): AppReduxState {

        val appState = state.copy()

        appState.callState = callStateReducer.reduce(
            state.callState,
            action
        )

        appState.remoteParticipantState = participantStateReducer.reduce(
            state.remoteParticipantState,
            action
        )

        appState.localParticipantState = localParticipantReducer.reduce(
            state.localParticipantState,
            action
        )

        appState.permissionState = permissionStateReducer.reduce(state.permissionState, action)
        appState.lifecycleState = lifecycleReducer.reduce(state.lifecycleState, action)
        appState.errorState = errorReducer.reduce(state.errorState, action)
        appState.navigationState = navigationReducer.reduce(state.navigationState, action)
        appState.audioSessionState = audioSessionReducer.reduce(state.audioSessionState, action)
        appState.visibilityState = pipReducer.reduce(state.visibilityState, action)
        appState.callDiagnosticsState = callDiagnosticsReducer.reduce(state.callDiagnosticsState, action)
        appState.toastNotificationState = toastNotificationReducer.reduce(state.toastNotificationState, action)
        appState.captionsState = captionsReducer.reduce(state.captionsState, action)
        appState.callScreenInfoHeaderState = callScreenInformationHeaderReducer.reduce(state.callScreenInfoHeaderState, action)
        appState.buttonState = buttonViewDataReducer.reduce(state.buttonState, action)
        appState.rttState = rttReducer.reduce(state.rttState, action)
        appState.deviceConfigurationState = deviceConfigurationReducer.reduce(state.deviceConfigurationState, action)
        return appState
    }
}
