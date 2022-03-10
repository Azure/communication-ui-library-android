// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup

import com.azure.android.communication.ui.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.NavigationAction
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope

internal class SetupViewModel(
    store: Store<ReduxState>,
    setupViewModelProvider: SetupViewModelFactory,
) :
    BaseViewModel(store) {

    private val warningsViewModel = setupViewModelProvider.provideWarningsViewModel()
    private val setupControlsViewModel = setupViewModelProvider.provideSetupControlsViewModel()
    private val localParticipantRendererViewModel =
        setupViewModelProvider.providePreviewAreaViewModel()
    private val audioDeviceListViewModel = setupViewModelProvider.provideAudioDeviceListViewModel()
    private val errorInfoViewModel = setupViewModelProvider.provideErrorInfoViewModel()
    private val setupGradientViewModel = setupViewModelProvider.provideSetupGradientViewModel()
    private val participantAvatarViewModel =
        setupViewModelProvider.provideParticipantAvatarViewModel()
    private val joinCallButtonHolderViewModel =
        setupViewModelProvider.provideJoinCallButtonHolderViewModel()

    fun getJoinCallButtonHolderViewModel() = joinCallButtonHolderViewModel

    fun getParticipantAvatarViewModel() = participantAvatarViewModel

    fun getSetupGradientViewViewModel() = setupGradientViewModel

    fun getErrorInfoViewModel() = errorInfoViewModel

    fun getLocalParticipantRendererViewModel() = localParticipantRendererViewModel

    fun getAudioDeviceListViewModel() = audioDeviceListViewModel

    fun getWarningsViewModel() = warningsViewModel

    fun getSetupControlsViewModel() = setupControlsViewModel

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
        errorInfoViewModel.update(state.errorState)
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
