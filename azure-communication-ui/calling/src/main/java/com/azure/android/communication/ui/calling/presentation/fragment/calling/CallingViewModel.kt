// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import android.util.Log
import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope

internal class CallingViewModel(
    store: Store<ReduxState>,
    callingViewModelProvider: CallingViewModelFactory,
) :
    BaseViewModel(store) {

    val moreCallOptionsListViewModel = callingViewModelProvider.moreCallOptionsListViewModel
    val participantGridViewModel = callingViewModelProvider.participantGridViewModel
    val controlBarViewModel = callingViewModelProvider.controlBarViewModel
    val confirmLeaveOverlayViewModel = callingViewModelProvider.confirmLeaveOverlayViewModel
    val localParticipantViewModel = callingViewModelProvider.localParticipantViewModel
    val floatingHeaderViewModel = callingViewModelProvider.floatingHeaderViewModel
    val audioDeviceListViewModel = callingViewModelProvider.audioDeviceListViewModel
    val participantListViewModel = callingViewModelProvider.participantListViewModel
    val bannerViewModel = callingViewModelProvider.bannerViewModel
    val waitingLobbyOverlayViewModel = callingViewModelProvider.waitingLobbyOverlayViewModel
    val connectingLobbyOverlayViewModel = callingViewModelProvider.connectingLobbyOverlayViewModel
    val holdOverlayViewModel = callingViewModelProvider.onHoldOverlayViewModel
    val errorInfoViewModel = callingViewModelProvider.snackBarViewModel

    fun switchFloatingHeader() {
        floatingHeaderViewModel.switchFloatingHeader()
    }

    fun requestCallEnd() {
        confirmLeaveOverlayViewModel.requestExitConfirmation()
    }

    override fun init(coroutineScope: CoroutineScope) {
        val state = store.getCurrentState()
        Log.d("Mohtasim", "Calling screen:: state: ${state.callState.callingStatus}")
        controlBarViewModel.init(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            this::requestCallEnd,
            audioDeviceListViewModel::displayAudioDeviceSelectionMenu,
            moreCallOptionsListViewModel::display,
        )

        localParticipantViewModel.init(
            state.localParticipantState.displayName,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.videoStreamID,
            state.remoteParticipantState.participantMap.count(),
            state.callState.callingStatus,
            state.localParticipantState.cameraState.device,
            state.localParticipantState.cameraState.camerasCount,
        )

        floatingHeaderViewModel.init(
            state.callState.callingStatus,
            state.remoteParticipantState.participantMap.count()
        )
        audioDeviceListViewModel.init(
            state.localParticipantState.audioState
        )
        bannerViewModel.init(
            state.callState
        )

        participantListViewModel.init(
            state.remoteParticipantState.participantMap,
            state.localParticipantState
        )

        Log.d("Mohtasim", "CallingState = ${state.callState.callingStatus.name}")
        waitingLobbyOverlayViewModel.init(state.callState.callingStatus)
        connectingLobbyOverlayViewModel.init(state.callState.callingStatus)

        holdOverlayViewModel.init(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.init(state.callState.callingStatus)

        super.init(coroutineScope)
    }

    override suspend fun onStateChange(state: ReduxState) {
        Log.d("Mohtasim", "Calling screen:: state: ${state.callState.callingStatus}")
        if (state.lifecycleState.state == LifecycleStatus.BACKGROUND) {
            participantGridViewModel.clear()
            localParticipantViewModel.clear()
            return
        }

        controlBarViewModel.update(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState.callingStatus
        )

        localParticipantViewModel.update(
            state.localParticipantState.displayName,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.videoStreamID,
            state.remoteParticipantState.participantMap.count(),
            state.callState.callingStatus,
            state.localParticipantState.cameraState.device,
            state.localParticipantState.cameraState.camerasCount,
        )

        audioDeviceListViewModel.update(
            state.localParticipantState.audioState,
        )

        Log.d("Mohtasim", "CallingState = ${state.callState.callingStatus.name}")
        waitingLobbyOverlayViewModel.update(state.callState.callingStatus)
        connectingLobbyOverlayViewModel.update(state.callState.callingStatus)
        holdOverlayViewModel.update(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.updateIsLobbyOverlayDisplayed(state.callState.callingStatus)

        if (state.callState.callingStatus == CallingStatus.LOCAL_HOLD) {
            participantGridViewModel.update(
                System.currentTimeMillis(),
                mapOf(),
            )
            floatingHeaderViewModel.dismiss()
            participantListViewModel.closeParticipantList()
            localParticipantViewModel.update(
                state.localParticipantState.displayName,
                state.localParticipantState.audioState.operation,
                state.localParticipantState.videoStreamID,
                0,
                state.callState.callingStatus,
                state.localParticipantState.cameraState.device,
                state.localParticipantState.cameraState.camerasCount,
            )
        }

        if (shouldUpdateRemoteParticipantsViewModels(state)) {
            participantGridViewModel.update(
                state.remoteParticipantState.modifiedTimestamp,
                state.remoteParticipantState.participantMap,
            )

            floatingHeaderViewModel.update(
                state.remoteParticipantState.participantMap.count()
            )

            participantListViewModel.update(
                state.remoteParticipantState.participantMap,
                state.localParticipantState
            )

            bannerViewModel.update(state.callState)

            state.localParticipantState.cameraState.error?.let {
                errorInfoViewModel.updateCallCompositeError(it)
            }
        }
        updateOverlayDisplayedState(state.callState.callingStatus)
    }

    private fun shouldUpdateRemoteParticipantsViewModels(state: ReduxState) =
        state.callState.callingStatus == CallingStatus.CONNECTED

    private fun updateOverlayDisplayedState(callingStatus: CallingStatus) {
        floatingHeaderViewModel.updateIsOverlayDisplayed(callingStatus)
        bannerViewModel.updateIsOverlayDisplayed(callingStatus)
        localParticipantViewModel.updateIsOverlayDisplayed(callingStatus)
    }
}
