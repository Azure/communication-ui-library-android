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
import com.azure.android.communication.ui.calling.redux.action.RttAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RttState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    private var isCaptionsVisibleMutableFlow = MutableStateFlow(false)
    // This is a flag to ensure that the call is started only once
    // This is to avoid a lag between updating isDefaultParametersCallStarted
    private var callStartRequested = false

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
    val isCaptionsVisibleFlow: StateFlow<Boolean> = isCaptionsVisibleMutableFlow
    var isCaptionsMaximized: Boolean = false

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
            visibilityState = state.visibilityState,
            audioVideoMode = state.localParticipantState.audioVideoMode,
            capabilities = state.localParticipantState.capabilities,
            buttonViewDataState = state.buttonState,
            controlBarOptions = callScreenOptions?.controlBarOptions,
            deviceConfigurationState = state.deviceConfigurationState,
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
            avMode,
            isOverlayDisplayedOverGrid(state),
        )

        floatingHeaderViewModel.init(
            remoteParticipantsForGridView.count(),
            state.callScreenInfoHeaderState,
            state.buttonState,
            isOverlayDisplayedOverGrid(state),
            this::requestCallEndOnBackPressed,
            /* <CALL_START_TIME>
            state.callState.callStartTime,
            </CALL_START_TIME> */
        )

        audioDeviceListViewModel.init(
            state.localParticipantState.audioState,
            state.visibilityState
        )
        bannerViewModel.init(
            state.callState,
            isOverlayDisplayedOverGrid(state),
        )

        participantMenuViewModel.init(
            state.localParticipantState.capabilities,
        )

        participantListViewModel.init(
            state.remoteParticipantState.participantMap,
            state.localParticipantState,
            shouldShowLobby(
                state.localParticipantState.capabilities,
                state.visibilityState
            ),
            participantMenuViewModel::displayParticipantMenu,
            state.remoteParticipantState.totalParticipantCount,
        )

        waitingLobbyOverlayViewModel.init(shouldDisplayLobbyOverlay(state))

        connectingLobbyOverlayViewModel.init(
            state.callState,
            state.permissionState,
            networkManager,
            state.localParticipantState.cameraState,
            state.localParticipantState.audioState,
            state.localParticipantState.initialCallJoinState,
        )
        holdOverlayViewModel.init(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.init(
            state.rttState,
            isOverlayDisplayedOverGrid(state),
            state.deviceConfigurationState,
            state.captionsState,
        )

        lobbyHeaderViewModel.init(
            state.callState.callingStatus,
            getLobbyParticipantsForHeader(state),
            shouldShowLobby(
                state.localParticipantState.capabilities,
                state.visibilityState
            )
        )

        lobbyErrorHeaderViewModel.init(
            state.callState.callingStatus,
            state.remoteParticipantState.lobbyErrorCode,
            shouldShowLobby(
                state.localParticipantState.capabilities,
                state.visibilityState,
            )
        )

        captionsListViewModel.init(
            state.captionsState,
            state.callState.callingStatus,
            state.visibilityState,
            state.buttonState,
            state.rttState,
            state.navigationState,
        )
        captionsLanguageSelectionListViewModel.init(state.captionsState, state.visibilityState, state.navigationState)
        isCaptionsVisibleMutableFlow.value =
            shouldShowCaptionsUI(state.visibilityState, state.captionsState.status, state.rttState)
        captionsLayoutViewModel.init(
            state.captionsState,
            state.rttState,
            isCaptionsVisibleMutableFlow.value,
            state.deviceConfigurationState,
        )

        moreCallOptionsListViewModel.init(
            state.visibilityState,
            state.buttonState,
            state.navigationState
        )
        toastNotificationViewModel.init(
            coroutineScope,
        )
        isCaptionsMaximized = state.rttState.isMaximized
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
            state.buttonState,
            deviceConfigurationState = state.deviceConfigurationState,
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
            avMode,
            shouldDisplayLobbyOverlay(state),
        )

        audioDeviceListViewModel.update(
            state.localParticipantState.audioState,
            state.visibilityState
        )

        waitingLobbyOverlayViewModel.update(shouldDisplayLobbyOverlay(state))
        connectingLobbyOverlayViewModel.update(
            state.callState,
            state.localParticipantState.cameraState.operation,
            state.permissionState,
            state.localParticipantState.audioState.operation,
            state.localParticipantState.initialCallJoinState
        )
        holdOverlayViewModel.update(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        if (state.callState.callingStatus == CallingStatus.LOCAL_HOLD) {
            participantGridViewModel.update(
                remoteParticipantsMapUpdatedTimestamp = System.currentTimeMillis(),
                remoteParticipantsMap = mapOf(),
                dominantSpeakersInfo = listOf(),
                dominantSpeakersModifiedTimestamp = System.currentTimeMillis(),
                visibilityStatus = state.visibilityState.status,
                rttState = state.rttState,
                isOverlayDisplayedOverGrid = isOverlayDisplayedOverGrid(state),
                deviceConfigurationState = state.deviceConfigurationState,
                captionsState = state.captionsState,
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
                avMode,
                shouldDisplayLobbyOverlay(state),
            )
        }

        if (shouldUpdateRemoteParticipantsViewModels(state)) {
            participantGridViewModel.update(
                remoteParticipantsMapUpdatedTimestamp = state.remoteParticipantState.participantMapModifiedTimestamp,
                remoteParticipantsMap = remoteParticipantsForGridView,
                dominantSpeakersInfo = state.remoteParticipantState.dominantSpeakersInfo,
                dominantSpeakersModifiedTimestamp = state.remoteParticipantState.dominantSpeakersModifiedTimestamp,
                visibilityStatus = state.visibilityState.status,
                rttState = state.rttState,
                isOverlayDisplayedOverGrid = isOverlayDisplayedOverGrid(state),
                deviceConfigurationState = state.deviceConfigurationState,
                captionsState = state.captionsState,
            )

            floatingHeaderViewModel.update(
                totalParticipantCountExceptHidden,
                state.callScreenInfoHeaderState,
                state.buttonState,
                isOverlayDisplayedOverGrid(state),
                /* <CALL_START_TIME>
                state.callState.callStartTime,
                </CALL_START_TIME> */
                state.visibilityState.status,
            )

            lobbyHeaderViewModel.update(
                state.callState.callingStatus,
                getLobbyParticipantsForHeader(state),
                shouldShowLobby(
                    state.localParticipantState.capabilities,
                    state.visibilityState
                )
            )

            lobbyErrorHeaderViewModel.update(
                state.callState.callingStatus,
                state.remoteParticipantState.lobbyErrorCode,
                shouldShowLobby(
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
                shouldShowLobby(
                    state.localParticipantState.capabilities,
                    state.visibilityState
                ),
                totalParticipantCountExceptHidden
            )

            bannerViewModel.update(
                state.callState,
                state.visibilityState,
                isOverlayDisplayedOverGrid(state),
            )
        }

        confirmLeaveOverlayViewModel.update(state.visibilityState)
        moreCallOptionsListViewModel.update(
            state.visibilityState,
            state.buttonState,
            state.navigationState
        )

        state.localParticipantState.cameraState.error?.let {
            errorInfoViewModel.updateCallCompositeError(it)
        }

        captionsListViewModel.update(
            state.captionsState,
            state.callState.callingStatus,
            state.visibilityState,
            state.buttonState,
            state.rttState,
            state.navigationState,
        )
        captionsLanguageSelectionListViewModel.update(
            state.captionsState,
            state.visibilityState,
            state.navigationState
        )

        isCaptionsVisibleMutableFlow.value = shouldShowCaptionsUI(
            state.visibilityState,
            state.captionsState.status,
            state.rttState,
        )
        captionsLayoutViewModel.update(
            captionsState = state.captionsState,
            rttState = state.rttState,
            isVisible = isCaptionsVisibleMutableFlow.value,
            deviceConfigurationState = state.deviceConfigurationState,
        )
        isCaptionsMaximized = state.rttState.isMaximized
    }

    private fun getLobbyParticipantsForHeader(state: ReduxState) =
        if (shouldShowLobby(state.localParticipantState.capabilities, state.visibilityState))
            state.remoteParticipantState.participantMap.filter { it.value.participantStatus == ParticipantStatus.IN_LOBBY }
        else mapOf()

    private fun shouldShowLobby(
        capabilities: Set<ParticipantCapabilityType>,
        visibilityState: VisibilityState,
    ): Boolean {
        if (visibilityState.status != VisibilityStatus.VISIBLE)
            return false

        return capabilitiesManager.hasCapability(capabilities, ParticipantCapabilityType.MANAGE_LOBBY)
    }

    private fun remoteParticipantsForGridView(participants: Map<String, ParticipantInfoModel>): Map<String, ParticipantInfoModel> =
        participants.filter {
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

    private fun leaveCallWithoutConfirmation() {
        confirmLeaveOverlayViewModel.confirm()
    }

    fun shouldShowCaptionsUI(
        visibilityState: VisibilityState,
        captionsStatus: CaptionsStatus,
        rttState: RttState,
    ) =
        visibilityState.status == VisibilityStatus.VISIBLE && (
            rttState.isRttActive ||
                captionsStatus == CaptionsStatus.STARTED ||
                captionsStatus == CaptionsStatus.START_REQUESTED ||
                captionsStatus == CaptionsStatus.STOP_REQUESTED
            )

    fun minimizeCaptions() {
        dispatchAction(RttAction.UpdateMaximized(false))
    }

    private fun shouldDisplayLobbyOverlay(state: ReduxState) =
        state.callState.callingStatus == CallingStatus.IN_LOBBY

    private fun isOverlayDisplayedOverGrid(state: ReduxState): Boolean {
        return shouldDisplayLobbyOverlay(state) ||
            state.callState.callingStatus == CallingStatus.LOCAL_HOLD ||
            state.rttState.isMaximized
    }
}
