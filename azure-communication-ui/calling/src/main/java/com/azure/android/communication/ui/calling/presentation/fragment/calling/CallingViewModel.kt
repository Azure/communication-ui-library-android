// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeInternalParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.CoroutineScope

internal class CallingViewModel(
    store: Store<ReduxState>,
    callingViewModelProvider: CallingViewModelFactory,
    private val networkManager: NetworkManager,
    val multitaskingEnabled: Boolean,
    val avMode: CallCompositeAudioVideoMode,
) :
    BaseViewModel(store) {

    val moreCallOptionsListViewModel = callingViewModelProvider.moreCallOptionsListViewModel
    val participantGridViewModel = callingViewModelProvider.participantGridViewModel
    val controlBarViewModel = callingViewModelProvider.controlBarViewModel
    val confirmLeaveOverlayViewModel = callingViewModelProvider.confirmLeaveOverlayViewModel
    val localParticipantViewModel = callingViewModelProvider.localParticipantViewModel
    val floatingHeaderViewModel = callingViewModelProvider.floatingHeaderViewModel
    val upperMessageBarNotificationLayoutViewModel = callingViewModelProvider.upperMessageBarNotificationLayoutViewModel
    val toastNotificationViewModel = callingViewModelProvider.toastNotificationViewModel
    val audioDeviceListViewModel = callingViewModelProvider.audioDeviceListViewModel
    val participantListViewModel = callingViewModelProvider.participantListViewModel
    val bannerViewModel = callingViewModelProvider.bannerViewModel
    val waitingLobbyOverlayViewModel = callingViewModelProvider.waitingLobbyOverlayViewModel
    val connectingLobbyOverlayViewModel = callingViewModelProvider.connectingLobbyOverlayViewModel
    val holdOverlayViewModel = callingViewModelProvider.onHoldOverlayViewModel
    val errorInfoViewModel = callingViewModelProvider.errorInfoViewModel
    val lobbyHeaderViewModel = callingViewModelProvider.lobbyHeaderViewModel
    val lobbyErrorHeaderViewModel = callingViewModelProvider.lobbyErrorHeaderViewModel

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
            state.localUserState.cameraState,
            state.localUserState.audioState,
            state.callState,
            this::requestCallEnd,
            audioDeviceListViewModel::displayAudioDeviceSelectionMenu,
            moreCallOptionsListViewModel::display,
            state.visibilityState,
        )

        localParticipantViewModel.init(
            state.localUserState.displayName,
            state.localUserState.audioState.operation,
            state.localUserState.videoStreamID,
            remoteParticipantsForGridView.count(),
            state.callState.callStatus,
            state.localUserState.cameraState.device,
            state.localUserState.cameraState.camerasCount,
            state.visibilityState.status,
            avMode
        )

        floatingHeaderViewModel.init(
            state.callState.callStatus,
            remoteParticipantsForGridView.count(),
            this::requestCallEnd,
        )

        audioDeviceListViewModel.init(
            state.localUserState.audioState,
            state.visibilityState
        )
        bannerViewModel.init(
            state.callState
        )

        participantListViewModel.init(
            state.remoteParticipantState.participantMap,
            state.localUserState,
            canShowLobby(state.localUserState.localParticipantRole, state.visibilityState)
        )

        waitingLobbyOverlayViewModel.init(state.callState.callStatus)

        connectingLobbyOverlayViewModel.init(
            state.callState,
            state.permissionState,
            networkManager,
            state.localUserState.cameraState,
            state.localUserState.audioState,
            state.localUserState.initialCallJoinState,
        )
        holdOverlayViewModel.init(state.callState.callStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.init(state.callState.callStatus, state.visibilityState)

        lobbyHeaderViewModel.init(
            state.callState.callStatus,
            getLobbyParticipantsForHeader(state),
            canShowLobby(state.localUserState.localParticipantRole, state.visibilityState)
        )

        lobbyErrorHeaderViewModel.init(
            state.callState.callStatus,
            state.remoteParticipantState.lobbyErrorCode,
            canShowLobby(state.localUserState.localParticipantRole, state.visibilityState)
        )

        super.init(coroutineScope)
    }

    override suspend fun onStateChange(state: ReduxState) {
        if (!state.callState.isDefaultParametersCallStarted &&
            state.localUserState.initialCallJoinState.skipSetupScreen &&
            state.permissionState.audioPermissionState == PermissionStatus.GRANTED
        ) {
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
            state.localUserState.cameraState,
            state.localUserState.audioState,
            state.callState.callStatus,
            state.visibilityState,
        )

        localParticipantViewModel.update(
            state.localUserState.displayName,
            state.localUserState.audioState.operation,
            state.localUserState.videoStreamID,
            remoteParticipantsForGridView.count(),
            state.callState.callStatus,
            state.localUserState.cameraState.device,
            state.localUserState.cameraState.camerasCount,
            state.visibilityState.status,
            avMode
        )

        audioDeviceListViewModel.update(
            state.localUserState.audioState,
            state.visibilityState
        )

        waitingLobbyOverlayViewModel.update(state.callState.callStatus)
        connectingLobbyOverlayViewModel.update(
            state.callState,
            state.localUserState.cameraState.operation,
            state.permissionState,
            state.localUserState.audioState.operation,
            state.localUserState.initialCallJoinState
        )
        holdOverlayViewModel.update(state.callState.callStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.updateIsLobbyOverlayDisplayed(state.callState.callStatus)

        if (state.callState.callStatus == CallStatus.LOCAL_HOLD) {
            participantGridViewModel.update(
                remoteParticipantsMapUpdatedTimestamp = System.currentTimeMillis(),
                remoteParticipantsMap = mapOf(),
                dominantSpeakersInfo = listOf(),
                dominantSpeakersModifiedTimestamp = System.currentTimeMillis(),
                state.visibilityState.status,
            )
            floatingHeaderViewModel.dismiss()
            lobbyHeaderViewModel.dismiss()
            lobbyErrorHeaderViewModel.dismiss()
            participantListViewModel.closeParticipantList()
            localParticipantViewModel.update(
                state.localUserState.displayName,
                state.localUserState.audioState.operation,
                state.localUserState.videoStreamID,
                0,
                state.callState.callStatus,
                state.localUserState.cameraState.device,
                state.localUserState.cameraState.camerasCount,
                state.visibilityState.status,
                avMode
            )
        }

        if (shouldUpdateRemoteParticipantsViewModels(state)) {
            participantGridViewModel.update(
                state.remoteParticipantState.participantMapModifiedTimestamp,
                remoteParticipantsForGridView,
                state.remoteParticipantState.dominantSpeakersInfo,
                state.remoteParticipantState.dominantSpeakersModifiedTimestamp,
                state.visibilityState.status,
            )

            floatingHeaderViewModel.update(
                remoteParticipantsForGridView.count()
            )

            lobbyHeaderViewModel.update(
                state.callState.callStatus,
                getLobbyParticipantsForHeader(state),
                canShowLobby(
                    state.localUserState.localParticipantRole,
                    state.visibilityState
                )
            )

            lobbyErrorHeaderViewModel.update(
                state.callState.callStatus,
                state.remoteParticipantState.lobbyErrorCode,
                canShowLobby(
                    state.localUserState.localParticipantRole,
                    state.visibilityState
                )
            )

            upperMessageBarNotificationLayoutViewModel.update(
                state.callDiagnosticsState
            )

            toastNotificationViewModel.update(
                state.callDiagnosticsState
            )

            participantListViewModel.update(
                state.remoteParticipantState.participantMap,
                state.localUserState,
                state.visibilityState,
                canShowLobby(
                    state.localUserState.localParticipantRole,
                    state.visibilityState
                )
            )

            bannerViewModel.update(state.callState)
        }

        confirmLeaveOverlayViewModel.update(state.visibilityState)
        moreCallOptionsListViewModel.update(state.visibilityState)

        state.localUserState.cameraState.error?.let {
            errorInfoViewModel.updateCallCompositeError(it)
        }

        updateOverlayDisplayedState(state.callState.callStatus)
    }

    private fun getLobbyParticipantsForHeader(state: ReduxState) =
        if (canShowLobby(state.localUserState.localParticipantRole, state.visibilityState))
            state.remoteParticipantState.participantMap.filter { it.value.participantStatus == ParticipantStatus.IN_LOBBY }
        else mapOf()

    private fun canShowLobby(role: CallCompositeInternalParticipantRole?, visibilityState: VisibilityState): Boolean {
        if (visibilityState.status != VisibilityStatus.VISIBLE)
            return false

        role?.let {
            return it == CallCompositeInternalParticipantRole.ORGANIZER ||
                it == CallCompositeInternalParticipantRole.PRESENTER ||
                it == CallCompositeInternalParticipantRole.COORGANIZER
        }
        return false
    }

    private fun remoteParticipantsForGridView(participants: Map<String, ParticipantInfoModel>): Map<String, ParticipantInfoModel> =
        participants.filter {
            it.value.participantStatus != ParticipantStatus.DISCONNECTED &&
                it.value.participantStatus != ParticipantStatus.IN_LOBBY
        }

    private fun shouldUpdateRemoteParticipantsViewModels(state: ReduxState) =
        state.callState.callStatus == CallStatus.CONNECTED

    private fun updateOverlayDisplayedState(callStatus: CallStatus) {
        floatingHeaderViewModel.updateIsOverlayDisplayed(callStatus)
        bannerViewModel.updateIsOverlayDisplayed(callStatus)
        localParticipantViewModel.updateIsOverlayDisplayed(callStatus)
    }
}
