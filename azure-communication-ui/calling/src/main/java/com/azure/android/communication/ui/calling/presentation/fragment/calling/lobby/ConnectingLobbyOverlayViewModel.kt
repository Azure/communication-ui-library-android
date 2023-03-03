package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ConnectingLobbyOverlayViewModel(private val dispatch: (Action) -> Unit) {

    private lateinit var displayLobbyOverlayFlow: MutableStateFlow<Boolean>

    fun getDisplayLobbyOverlayFlow(): StateFlow<Boolean> = displayLobbyOverlayFlow

    fun init(
        callingState: CallingStatus,
        permissionState: PermissionState
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow = MutableStateFlow(displayLobbyOverlay)

        if (permissionState.audioPermissionState == PermissionStatus.NOT_ASKED) {
            requestAudioPermission()
        }
    }

    fun update(
        callingState: CallingStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState)
        displayLobbyOverlayFlow.value = displayLobbyOverlay
    }

    private fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    private fun shouldDisplayLobbyOverlay(callingStatus: CallingStatus) =
        (callingStatus == CallingStatus.NONE) ||
            (callingStatus == CallingStatus.CONNECTING)
}
