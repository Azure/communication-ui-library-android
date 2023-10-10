// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import com.azure.android.communication.ui.calling.models.CallCompositeInternalParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope

internal class CallingViewModel(
    store: Store<ReduxState>,
    callingViewModelProvider: CallingViewModelFactory,
    private val networkManager: NetworkManager,
    val multitaskingEnabled: Boolean
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
    val errorInfoViewModel = callingViewModelProvider.errorInfoViewModel
    val lobbyHeaderViewModel = callingViewModelProvider.lobbyHeaderViewModel
    val lobbyErrorHeaderViewModel = callingViewModelProvider.lobbyErrorHeaderViewModel

    private var hasSetupCalled = false

    fun switchFloatingHeader() {
        floatingHeaderViewModel.switchFloatingHeader()
    }

    fun requestCallEnd() {
        confirmLeaveOverlayViewModel.requestExitConfirmation()
    }

    override fun init(coroutineScope: CoroutineScope) {
        val state = store.getCurrentState()
        val remoteParticipantsForGridView = remoteParticipantsForGridView(state.remoteParticipantState.participantMap)

        controlBarViewModel.init(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState,
            this::requestCallEnd,
            audioDeviceListViewModel::displayAudioDeviceSelectionMenu,
            moreCallOptionsListViewModel::display,
            state.pipState,
        )

        localParticipantViewModel.init(
            state.localParticipantState.displayName,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.videoStreamID,
            remoteParticipantsForGridView.count(),
            state.callState.callingStatus,
            state.localParticipantState.cameraState.device,
            state.localParticipantState.cameraState.camerasCount,
            state.pipState.status,
        )

        floatingHeaderViewModel.init(
            state.callState.callingStatus,
            remoteParticipantsForGridView.count(),
            this::requestCallEnd
        )
        audioDeviceListViewModel.init(
            state.localParticipantState.audioState
        )
        bannerViewModel.init(
            state.callState
        )

        participantListViewModel.init(
            remoteParticipantsForGridView,
            state.localParticipantState,
            canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status)
        )

        waitingLobbyOverlayViewModel.init(state.callState.callingStatus)

        connectingLobbyOverlayViewModel.init(
            state.callState,
            state.permissionState,
            networkManager,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
        )
        holdOverlayViewModel.init(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.init(state.callState.callingStatus)

        lobbyHeaderViewModel.init(
            state.callState.callingStatus,
            getLobbyParticipantsForHeader(state),
            canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status)
        )

        lobbyErrorHeaderViewModel.init(
            state.callState.callingStatus,
            state.remoteParticipantState.lobbyErrorCode,
            canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status)
        )

        super.init(coroutineScope)
    }

    override suspend fun onStateChange(state: ReduxState) {

        if (!hasSetupCalled &&
            state.callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN &&
            state.permissionState.audioPermissionState == PermissionStatus.GRANTED
        ) {
            hasSetupCalled = true
            store.dispatch(action = CallingAction.CallRequestedWithoutSetup())
        }

        if (state.lifecycleState.state == LifecycleStatus.BACKGROUND) {
            participantGridViewModel.clear()
            localParticipantViewModel.clear()
            return
        }

        val remoteParticipantsForGridView = remoteParticipantsForGridView(state.remoteParticipantState.participantMap)

        controlBarViewModel.update(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState.callingStatus,
            state.pipState,
        )

        localParticipantViewModel.update(
            state.localParticipantState.displayName,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.videoStreamID,
            remoteParticipantsForGridView.count(),
            state.callState.callingStatus,
            state.localParticipantState.cameraState.device,
            state.localParticipantState.cameraState.camerasCount,
            state.pipState.status,
        )

        audioDeviceListViewModel.update(
            state.localParticipantState.audioState,
        )

        waitingLobbyOverlayViewModel.update(state.callState.callingStatus)
        connectingLobbyOverlayViewModel.update(
            state.callState,
            state.localParticipantState.cameraState.operation,
            state.permissionState,
            state.localParticipantState.audioState.operation,
        )
        holdOverlayViewModel.update(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.updateIsLobbyOverlayDisplayed(state.callState.callingStatus)

        if (state.callState.callingStatus == CallingStatus.LOCAL_HOLD) {
            participantGridViewModel.update(
                remoteParticipantsMapUpdatedTimestamp = System.currentTimeMillis(),
                remoteParticipantsMap = mapOf(),
                dominantSpeakersInfo = listOf(),
                dominantSpeakersModifiedTimestamp = System.currentTimeMillis(),
                state.pipState.status,
            )
            floatingHeaderViewModel.dismiss()
            lobbyHeaderViewModel.dismiss()
            lobbyErrorHeaderViewModel.dismiss()
            participantListViewModel.closeParticipantList()
            localParticipantViewModel.update(
                state.localParticipantState.displayName,
                state.localParticipantState.audioState.operation,
                state.localParticipantState.videoStreamID,
                0,
                state.callState.callingStatus,
                state.localParticipantState.cameraState.device,
                state.localParticipantState.cameraState.camerasCount,
                state.pipState.status,
            )
        }

        if (shouldUpdateRemoteParticipantsViewModels(state)) {
            participantGridViewModel.update(
                state.remoteParticipantState.participantMapModifiedTimestamp,
                remoteParticipantsForGridView,
                state.remoteParticipantState.dominantSpeakersInfo,
                state.remoteParticipantState.dominantSpeakersModifiedTimestamp,
                state.pipState.status,
            )

            floatingHeaderViewModel.update(
                remoteParticipantsForGridView.count()
            )

            lobbyHeaderViewModel.update(
                state.callState.callingStatus,
                getLobbyParticipantsForHeader(state),
                canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status)
            )

            lobbyErrorHeaderViewModel.update(
                state.callState.callingStatus,
                state.remoteParticipantState.lobbyErrorCode,
                canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status)
            )

            participantListViewModel.update(
                state.remoteParticipantState.participantMap,
                state.localParticipantState,
                canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status)
            )

            bannerViewModel.update(state.callState)

            state.localParticipantState.cameraState.error?.let {
                errorInfoViewModel.updateCallCompositeError(it)
            }
        }
        updateOverlayDisplayedState(state.callState.callingStatus)
    }

    private fun getLobbyParticipantsForHeader(state: ReduxState) =
        if (canShowLobby(state.localParticipantState.localParticipantRole, state.pipState.status))
            state.remoteParticipantState.participantMap.filter { it.value.participantStatus == ParticipantStatus.IN_LOBBY }
        else mapOf()

    private fun canShowLobby(role: CallCompositeInternalParticipantRole?, pipMode: PictureInPictureStatus): Boolean {
        role?.let {
            return pipMode != PictureInPictureStatus.PIP_MODE_ENTERED && (
                it == CallCompositeInternalParticipantRole.ORGANIZER ||
                    it == CallCompositeInternalParticipantRole.PRESENTER ||
                    it == CallCompositeInternalParticipantRole.COORGANIZER
                )
        }
        return false
    }

    private fun remoteParticipantsForGridView(participants: Map<String, ParticipantInfoModel>): Map<String, ParticipantInfoModel> =
        participants.filter {
            it.value.participantStatus == ParticipantStatus.CONNECTED ||
                it.value.participantStatus == ParticipantStatus.HOLD
        }

    private fun shouldUpdateRemoteParticipantsViewModels(state: ReduxState) =
        state.callState.callingStatus == CallingStatus.CONNECTED

    private fun updateOverlayDisplayedState(callingStatus: CallingStatus) {
        floatingHeaderViewModel.updateIsOverlayDisplayed(callingStatus)
        bannerViewModel.updateIsOverlayDisplayed(callingStatus)
        localParticipantViewModel.updateIsOverlayDisplayed(callingStatus)
    }
}
