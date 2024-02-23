// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.FatalError
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
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
        callingState: CallingState,
        permissionState: PermissionState,
        networkManager: NetworkManager,
        cameraState: CameraState,
        audioState: AudioState,
    ) {
        this.networkManager = networkManager
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState, permissionState)
        displayLobbyOverlayFlow = MutableStateFlow(displayLobbyOverlay)

        cameraStateFlow = MutableStateFlow(cameraState.operation)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        if (displayLobbyOverlay) {
            handleOffline(this.networkManager)
        }
        if (permissionState.audioPermissionState == PermissionStatus.NOT_ASKED) {
            requestAudioPermission()
        }
    }

    fun update(
        callingState: CallingState,
        cameraOperationalStatus: CameraOperationalStatus,
        permissionState: PermissionState,
        audioOperationalStatus: AudioOperationalStatus,
    ) {
        val displayLobbyOverlay = shouldDisplayLobbyOverlay(callingState, permissionState)
        displayLobbyOverlayFlow.value = displayLobbyOverlay

        audioOperationalStatusStateFlow.value = audioOperationalStatus
        cameraStateFlow.value = cameraOperationalStatus

        handlePermissionDeniedEvent(permissionState)
        if (displayLobbyOverlay) {
            handleOffline(this.networkManager)
        }
    }

    fun getCameraStateFlow(): StateFlow<CameraOperationalStatus> {
        return cameraStateFlow
    }

    fun handleMicrophoneAccessFailed() {
        dispatchAction(
            action = ErrorAction.FatalErrorOccurred(
                FatalError(
                    Throwable(),
                    ErrorCode.MICROPHONE_NOT_AVAILABLE
                )
            )
        )
    }

    private fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    private fun shouldDisplayLobbyOverlay(callingState: CallingState, permissionState: PermissionState) =
        ((callingState.callStatus == CallStatus.NONE) || (callingState.callStatus == CallStatus.CONNECTING)) &&
            (permissionState.audioPermissionState != PermissionStatus.DENIED) &&
            (callingState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN)

    private fun handleOffline(networkManager: NetworkManager) {
        if (!networkManager.isNetworkConnectionAvailable()) {
            dispatchAction(
                action = ErrorAction.FatalErrorOccurred(
                    FatalError(
                        Throwable(),
                        ErrorCode.INTERNET_NOT_AVAILABLE
                    )
                )
            )
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
