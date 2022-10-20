// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup

import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope

internal class SetupViewModel(
    store: Store<ReduxState>,
    private val setupViewModelProvider: SetupViewModelFactory,
) :
    BaseViewModel(store) {

    val warningsViewModel = setupViewModelProvider.warningsViewModel
    val setupControlsViewModel = setupViewModelProvider.setupControlsViewModel
    val localParticipantRendererViewModel = setupViewModelProvider.previewAreaViewModel
    val audioDeviceListViewModel = setupViewModelProvider.audioDeviceListViewModel
    val errorInfoViewModel = setupViewModelProvider.snackBarViewModel
    val setupGradientViewModel = setupViewModelProvider.setupGradientViewModel
    val participantAvatarViewModel = setupViewModelProvider.participantAvatarViewModel
    val joinCallButtonHolderViewModel = setupViewModelProvider.joinCallButtonHolderViewModel
    val controlBarMoreMenuViewModel = setupViewModelProvider.controlBarMoreMenuViewModel

    val displayName: String?
        get() = store.getCurrentState().localParticipantState.displayName

    fun setupCall() {
        dispatchAction(action = CallingAction.SetupCall())
    }

    fun exitComposite() {
        dispatchAction(action = CallingAction.CallEndRequested())
        dispatchAction(action = NavigationAction.Exit())
    }

    override fun init(coroutineScope: CoroutineScope) {
        val state = store.getCurrentState()
        warningsViewModel.init(state.permissionState)
        localParticipantRendererViewModel.init(
            state.localParticipantState.videoStreamID,
        )
        setupControlsViewModel.init(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState,
            audioDeviceListViewModel::displayAudioDeviceSelectionMenu
        )
        audioDeviceListViewModel.init(
            state.localParticipantState.audioState,
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

        joinCallButtonHolderViewModel.init(state.permissionState.audioPermissionState)

        super.init(coroutineScope)
    }

    override suspend fun onStateChange(state: ReduxState) {
        setupControlsViewModel.update(
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
            state.localParticipantState.audioState
        )
        errorInfoViewModel.updateCallStateError(state.errorState)
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
            state.callState
        )
    }
}
