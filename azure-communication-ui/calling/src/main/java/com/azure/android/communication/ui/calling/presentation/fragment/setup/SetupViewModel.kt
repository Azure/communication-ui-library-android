// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup

import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.AudioFocusStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope

internal class SetupViewModel(
    store: Store<ReduxState>,
    setupViewModelProvider: SetupViewModelFactory,
    private val networkManager: NetworkManager,
) :
    BaseViewModel(store) {

    val warningsViewModel = setupViewModelProvider.warningsViewModel
    val setupControlBarViewModel = setupViewModelProvider.setupControlBarViewModel
    val localParticipantRendererViewModel = setupViewModelProvider.previewAreaViewModel
    val audioDeviceListViewModel = setupViewModelProvider.audioDeviceListViewModel
    val errorInfoViewModel = setupViewModelProvider.errorInfoViewModel
    val setupGradientViewModel = setupViewModelProvider.setupGradientViewModel
    val participantAvatarViewModel = setupViewModelProvider.participantAvatarViewModel
    val joinCallButtonHolderViewModel = setupViewModelProvider.joinCallButtonHolderViewModel

    val displayName: String?
        get() = store.getCurrentState().localParticipantState.displayName

    fun setupCall() {
        dispatchAction(action = CallingAction.SetupCall())
    }

    fun exitComposite() {
        // double check here if we need both the action to execute
        dispatchAction(action = CallingAction.CallEndRequested())
        dispatchAction(action = NavigationAction.Exit())
    }

    override fun init(coroutineScope: CoroutineScope) {
        val state = store.getCurrentState()
        if (store.getCurrentState().localParticipantState.initialCallJoinState.startWithMicrophoneOn) {
            store.dispatch(action = LocalParticipantAction.MicPreviewOnTriggered())
        }
        if (store.getCurrentState().localParticipantState.initialCallJoinState.startWithCameraOn) {
            store.dispatch(action = LocalParticipantAction.CameraPreviewOnRequested())
        }

        warningsViewModel.init(state.permissionState)
        localParticipantRendererViewModel.init(
            state.localParticipantState.videoStreamID,
        )
        setupControlBarViewModel.init(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState,
            audioDeviceListViewModel::displayAudioDeviceSelectionMenu,
        )
        audioDeviceListViewModel.init(
            state.localParticipantState.audioState,
            state.visibilityState
        )
        setupGradientViewModel.init(
            state.localParticipantState.videoStreamID,
            state.localParticipantState.cameraState.operation
        )
        participantAvatarViewModel.init(
            state.localParticipantState.displayName,
            state.localParticipantState.videoStreamID,
            state.permissionState,
        )
        joinCallButtonHolderViewModel.init(
            state.permissionState.audioPermissionState,
            state.permissionState.cameraPermissionState,
            state.localParticipantState.cameraState.operation,
            state.localParticipantState.cameraState.camerasCount,
            networkManager
        )

        super.init(coroutineScope)
    }

    override suspend fun onStateChange(state: ReduxState) {

        setupControlBarViewModel.update(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState,
        )
        warningsViewModel.update(state.permissionState)
        localParticipantRendererViewModel.update(
            state.localParticipantState.videoStreamID,
        )
        audioDeviceListViewModel.update(
            state.localParticipantState.audioState,
            state.visibilityState
        )
        errorInfoViewModel.updateCallStateError(state.errorState)
        errorInfoViewModel.updateAudioFocusRejectedState(
            state.audioSessionState.audioFocusStatus == AudioFocusStatus.REJECTED
        )
        state.localParticipantState.cameraState.error?.let {
            errorInfoViewModel.updateCallCompositeError(it)
        }
        setupGradientViewModel.update(
            state.localParticipantState.videoStreamID,
            state.localParticipantState.cameraState.operation
        )
        participantAvatarViewModel.update(
            state.localParticipantState.videoStreamID,
            state.permissionState
        )
        joinCallButtonHolderViewModel.update(
            state.permissionState.audioPermissionState,
            state.callState,
            state.permissionState.cameraPermissionState,
            state.localParticipantState.cameraState.operation,
            state.localParticipantState.cameraState.camerasCount
        )
    }
}
