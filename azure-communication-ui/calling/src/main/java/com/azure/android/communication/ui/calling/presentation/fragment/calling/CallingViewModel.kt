// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLeaveCallConfirmationMode
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
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
    private val callScreenOptions: CallCompositeCallScreenOptions? = null,
    val multitaskingEnabled: Boolean,
    val avMode: CallCompositeAudioVideoMode,
    private val callType: CallType? = null,
    private val capabilitiesManager: CapabilitiesManager,
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
    val connectingLobbyOverlayViewModel = callingViewModelProvider.connectingOverlayViewModel
    val holdOverlayViewModel = callingViewModelProvider.onHoldOverlayViewModel
    val errorInfoViewModel = callingViewModelProvider.errorInfoViewModel
    val lobbyHeaderViewModel = callingViewModelProvider.lobbyHeaderViewModel
    val lobbyErrorHeaderViewModel = callingViewModelProvider.lobbyErrorHeaderViewModel
    val participantMenuViewModel = callingViewModelProvider.participantMenuViewModel
    val captionsListViewModel = callingViewModelProvider.captionsListViewModel
    val captionsLanguageSelectionListViewModel = callingViewModelProvider.captionsLanguageSelectionListViewModel
    val captionsLayoutViewModel = callingViewModelProvider.captionsViewModel
    /* <RTT_POC>
    val rttViewModel = callingViewModelProvider.rttViewModel
    </RTT_POC> */

    // This is a flag to ensure that the call is started only once
    // This is to avoid a lag between updating isDefaultParametersCallStarted
    private var callStartRequested = false

    fun switchFloatingHeader() {
        floatingHeaderViewModel.switchFloatingHeader()
    }

    fun requestCallEndOnBackPressed() {
        confirmLeaveOverlayViewModel.requestExitConfirmation()
    }

    fun requestCallEnd() {
        callScreenOptions?.controlBarOptions?.leaveCallConfirmation?.let {
            if (it == CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED) {
                confirmLeaveOverlayViewModel.requestExitConfirmation()
            } else {
                leaveCallWithoutConfirmation()
            }
        }
            // Default to always enabled
            ?: confirmLeaveOverlayViewModel.requestExitConfirmation()
    }

    override fun init(coroutineScope: CoroutineScope) {
        val state = store.getCurrentState()
        val remoteParticipantsForGridView = remoteParticipantsForGridView(state.remoteParticipantState.participantMap)

        controlBarViewModel.init(
            permissionState = state.permissionState,
            cameraState = state.localParticipantState.cameraState,
            audioState = state.localParticipantState.audioState,
            callState = state.callState,
            requestCallEndCallback = this::requestCallEnd,
            openAudioDeviceSelectionMenuCallback = audioDeviceListViewModel::displayAudioDeviceSelectionMenu,
            openMoreMenuCallback = moreCallOptionsListViewModel::display,
            visibilityState = state.visibilityState,
            audioVideoMode = state.localParticipantState.audioVideoMode,
            capabilities = state.localParticipantState.capabilities,
            callScreenOptions?.controlBarOptions,
        )

        localParticipantViewModel.init(
            state.localParticipantState.displayName,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.videoStreamID,
            remoteParticipantsForGridView.count(),
            state.callState.callingStatus,
            state.localParticipantState.cameraState.device,
            state.localParticipantState.cameraState.camerasCount,
            state.visibilityState.status,
            avMode
        )

        floatingHeaderViewModel.init(
            state.callState.callingStatus,
            remoteParticipantsForGridView.count(),
            /* <CUSTOM_CALL_HEADER> */
            state.callScreenInfoHeaderState,
            /* </CUSTOM_CALL_HEADER> */
            this::requestCallEndOnBackPressed,
        )

        audioDeviceListViewModel.init(
            state.localParticipantState.audioState,
            state.visibilityState
        )
        bannerViewModel.init(
            state.callState
        )

        participantMenuViewModel.init(
            state.localParticipantState.capabilities,
        )

        participantListViewModel.init(
            state.remoteParticipantState.participantMap,
            state.localParticipantState,
            canShowLobby(
                state.localParticipantState.capabilities,
                state.visibilityState
            ),
            participantMenuViewModel::displayParticipantMenu,
            state.remoteParticipantState.totalParticipantCount,
        )

        waitingLobbyOverlayViewModel.init(state.callState.callingStatus)

        connectingLobbyOverlayViewModel.init(
            state.callState,
            state.permissionState,
            networkManager,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.localParticipantState.initialCallJoinState,
        )
        holdOverlayViewModel.init(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.init(state.callState.callingStatus)

        lobbyHeaderViewModel.init(
            state.callState.callingStatus,
            getLobbyParticipantsForHeader(state),
            canShowLobby(
                state.localParticipantState.capabilities,
                state.visibilityState
            )
        )

        lobbyErrorHeaderViewModel.init(
            state.callState.callingStatus,
            state.remoteParticipantState.lobbyErrorCode,
            canShowLobby(
                state.localParticipantState.capabilities,
                state.visibilityState,
            )
        )
        /* <RTT_POC>
        rttViewModel.init(
            state.rttState.messages,
            state.rttState.isRttActive
        )
        </RTT_POC> */

        captionsListViewModel.init(state.captionsState, state.callState.callingStatus)
        captionsLanguageSelectionListViewModel.init(state.captionsState)
        captionsLayoutViewModel.init(state.captionsState, state.visibilityState)

        moreCallOptionsListViewModel.init(state.visibilityState)
        super.init(coroutineScope)
    }

    override suspend fun onStateChange(state: ReduxState) {
        if (!state.callState.isDefaultParametersCallStarted &&
            state.localParticipantState.initialCallJoinState.skipSetupScreen &&
            state.permissionState.audioPermissionState == PermissionStatus.GRANTED &&
            !callStartRequested
        ) {
            callStartRequested = true
            store.dispatch(action = CallingAction.CallRequestedWithoutSetup())
        }

        if (state.lifecycleState.state == LifecycleStatus.BACKGROUND) {
            participantGridViewModel.clear()
            localParticipantViewModel.clear()
            return
        }

        val remoteParticipantsForGridView = remoteParticipantsForGridView(state.remoteParticipantState.participantMap)
        val remoteParticipantsInAllStatesCount = state.remoteParticipantState.participantMap.count()
        val hiddenRemoteParticipantsCount = remoteParticipantsInAllStatesCount - remoteParticipantsForGridView.count()
        val totalParticipantCountExceptHidden = state.remoteParticipantState.totalParticipantCount - hiddenRemoteParticipantsCount

        controlBarViewModel.update(
            state.permissionState,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.callState.callingStatus,
            state.visibilityState,
            state.localParticipantState.audioVideoMode,
            state.localParticipantState.capabilities,
        )

        localParticipantViewModel.update(
            state.localParticipantState.displayName,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.videoStreamID,
            remoteParticipantsForGridView.count(),
            state.callState.callingStatus,
            state.localParticipantState.cameraState.device,
            state.localParticipantState.cameraState.camerasCount,
            state.visibilityState.status,
            avMode
        )

        audioDeviceListViewModel.update(
            state.localParticipantState.audioState,
            state.visibilityState
        )

        waitingLobbyOverlayViewModel.update(state.callState.callingStatus)
        connectingLobbyOverlayViewModel.update(
            state.callState,
            state.localParticipantState.cameraState.operation,
            state.permissionState,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.initialCallJoinState
        )
        holdOverlayViewModel.update(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.updateIsLobbyOverlayDisplayed(state.callState.callingStatus)

        if (state.callState.callingStatus == CallingStatus.LOCAL_HOLD) {
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
                state.localParticipantState.displayName,
                state.localParticipantState.audioState.operation,
                state.localParticipantState.videoStreamID,
                0,
                state.callState.callingStatus,
                state.localParticipantState.cameraState.device,
                state.localParticipantState.cameraState.camerasCount,
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
                totalParticipantCountExceptHidden,
                /* <CUSTOM_CALL_HEADER> */
                state.callScreenInfoHeaderState
                /* </CUSTOM_CALL_HEADER> */
            )

            lobbyHeaderViewModel.update(
                state.callState.callingStatus,
                getLobbyParticipantsForHeader(state),
                canShowLobby(
                    state.localParticipantState.capabilities,
                    state.visibilityState
                )
            )

            lobbyErrorHeaderViewModel.update(
                state.callState.callingStatus,
                state.remoteParticipantState.lobbyErrorCode,
                canShowLobby(
                    state.localParticipantState.capabilities,
                    state.visibilityState
                )
            )

            upperMessageBarNotificationLayoutViewModel.update(
                state.callDiagnosticsState
            )

            toastNotificationViewModel.update(
                state.toastNotificationState
            )

            participantMenuViewModel.update(
                state.localParticipantState.capabilities,
            )

            participantListViewModel.update(
                state.remoteParticipantState.participantMap,
                state.localParticipantState,
                state.visibilityState,
                canShowLobby(
                    state.localParticipantState.capabilities,
                    state.visibilityState
                ),
                totalParticipantCountExceptHidden
            )

            bannerViewModel.update(
                state.callState,
                state.visibilityState,
            )

            /* <RTT_POC>
            rttViewModel.update(
                state.rttState.messages,
                state.rttState.isRttActive
            )
            </RTT_POC> */
        }

        confirmLeaveOverlayViewModel.update(state.visibilityState)
        moreCallOptionsListViewModel.update(state.visibilityState)

        state.localParticipantState.cameraState.error?.let {
            errorInfoViewModel.updateCallCompositeError(it)
        }

        updateOverlayDisplayedState(state.callState.callingStatus)

        captionsListViewModel.update(state.captionsState, state.callState.callingStatus, state.visibilityState)
        captionsLanguageSelectionListViewModel.update(state.captionsState, state.visibilityState)
        captionsLayoutViewModel.update(state.captionsState, state.visibilityState)
    }

    private fun getLobbyParticipantsForHeader(state: ReduxState) =
        if (canShowLobby(state.localParticipantState.capabilities, state.visibilityState))
            state.remoteParticipantState.participantMap.filter { it.value.participantStatus == ParticipantStatus.IN_LOBBY }
        else mapOf()

    private fun canShowLobby(
        capabilities: Set<ParticipantCapabilityType>,
        visibilityState: VisibilityState,
    ): Boolean {
        if (visibilityState.status != VisibilityStatus.VISIBLE)
            return false

        return capabilitiesManager.hasCapability(capabilities, ParticipantCapabilityType.MANAGE_LOBBY)
    }

    private fun remoteParticipantsForGridView(participants: Map<String, ParticipantInfoModel>): Map<String, ParticipantInfoModel> =
        participants.filter {
            it.value.participantStatus != ParticipantStatus.DISCONNECTED &&
                it.value.participantStatus != ParticipantStatus.IN_LOBBY
        }

    private fun shouldUpdateRemoteParticipantsViewModels(state: ReduxState): Boolean {
        val isOutgoingCallInProgress = (
            state.callState.callingStatus == CallingStatus.RINGING ||
                state.callState.callingStatus == CallingStatus.CONNECTING
            ) &&
            callType == CallType.ONE_TO_N_OUTGOING
        val isOnRemoteHold = state.callState.callingStatus == CallingStatus.REMOTE_HOLD
        val isConnected = state.callState.callingStatus == CallingStatus.CONNECTED

        return isOutgoingCallInProgress || isOnRemoteHold || isConnected
    }

    private fun updateOverlayDisplayedState(callingStatus: CallingStatus) {
        floatingHeaderViewModel.updateIsOverlayDisplayed(callingStatus)
        bannerViewModel.updateIsOverlayDisplayed(callingStatus)
        localParticipantViewModel.updateIsOverlayDisplayed(callingStatus)
    }

    private fun leaveCallWithoutConfirmation() {
        confirmLeaveOverlayViewModel.confirm()
    }
}
