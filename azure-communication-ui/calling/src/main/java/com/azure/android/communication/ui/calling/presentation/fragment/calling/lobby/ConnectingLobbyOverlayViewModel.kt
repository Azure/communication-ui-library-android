package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.FatalError
import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.action.*
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.*
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ConnectingLobbyOverlayViewModel(private val dispatch: (Action) -> Unit) {

    private lateinit var displayLobbyOverlayFlow: MutableStateFlow<Boolean>
    private lateinit var networkManager: NetworkManager

    private lateinit var cameraStateFlow: MutableStateFlow<CameraOperationalStatus>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>

    fun getDisplayLobbyOverlayFlow(): StateFlow<Boolean> = displayLobbyOverlayFlow

    fun init(
        callingState: CallingStatus,
        permissionState: PermissionState,
        networkManager: NetworkManager,
        cameraState: CameraState,
        audioState: AudioState
    ) {
        this.networkManager = networkManager
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState, permissionState)
        displayLobbyOverlayFlow = MutableStateFlow(displayLobbyOverlay)

        cameraStateFlow = MutableStateFlow(cameraState.operation)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        handleOffline(this.networkManager)
        if (permissionState.audioPermissionState == PermissionStatus.NOT_ASKED) {
            requestAudioPermission()
        }
    }

    fun update(
        callingState: CallingStatus,
        cameraOperationalStatus: CameraOperationalStatus,
        permissionState: PermissionState,
        audioOperationalStatus: AudioOperationalStatus,
        readyToJoinCall: Boolean?,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState, permissionState)
        displayLobbyOverlayFlow.value = displayLobbyOverlay

        audioOperationalStatusStateFlow.value = audioOperationalStatus
        cameraStateFlow.value = cameraOperationalStatus

        handlePermissionDeniedEvent(permissionState)
        handleOffline(this.networkManager)

        if( readyToJoinCall == true && isAudioPermissionGranted(permissionState) &&
                   cameraOperationalStatus != CameraOperationalStatus.PENDING &&
                        shouldDisplayLobbyOverlay(callingState, permissionState) &&
                            callingState == CallingStatus.NONE) {

            // Potential error: Might want to make these actions serialized.
            dispatchAction(action = LocalParticipantAction.ToggleReadyToJoinCall())
            dispatchAction(action = CallingAction.CallStartRequested())
        }
    }

    private fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    private fun shouldDisplayLobbyOverlay(callingStatus: CallingStatus, permissionState: PermissionState) =
        ((callingStatus == CallingStatus.NONE) || (callingStatus == CallingStatus.CONNECTING)) &&
                (permissionState.audioPermissionState != PermissionStatus.DENIED)

    private fun isAudioPermissionGranted(permissionState: PermissionState) =
        (permissionState.audioPermissionState == PermissionStatus.GRANTED)

    private fun handleOffline(networkManager: NetworkManager) {
        if (!networkManager.isNetworkConnectionAvailable()) {
            dispatchAction(action = ErrorAction.CallStateErrorOccurred(
                CallStateError(
                    ErrorCode.NETWORK_NOT_AVAILABLE
                )
            ))
            dispatchAction(action = NavigationAction.Exit())
        }
    }

    private fun handlePermissionDeniedEvent(permissionState: PermissionState) {
        if (permissionState.audioPermissionState == PermissionStatus.DENIED) {
            dispatchAction(
                action = ErrorAction.FatalErrorOccurred(
                    FatalError(
                        Throwable(),
                        ErrorCode.MIC_PERMISSION_DENIED
                    )
                )
            )
        }
    }


}
