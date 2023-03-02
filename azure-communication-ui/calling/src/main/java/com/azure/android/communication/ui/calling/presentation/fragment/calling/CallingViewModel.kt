// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import android.util.Log
import com.azure.android.communication.ui.calling.presentation.fragment.BaseViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
    val singleThreadedContext = newSingleThreadContext("SingleThreadedContext")
    var track = false

    fun switchFloatingHeader() {
        floatingHeaderViewModel.switchFloatingHeader()
    }

    fun requestCallEnd() {
        // drop connecting overlay as well.
        confirmLeaveOverlayViewModel.requestExitConfirmation()
    }

    fun exitComposite() {
        dispatchAction(action = NavigationAction.Exit())
    }

    override fun init(coroutineScope: CoroutineScope) {
        val state = store.getCurrentState()

        defaultCallInit()
        if (state.callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN) {
            runBlocking {
                withContext(singleThreadedContext) {
                    track = false
                }
            }
        }

        super.init(coroutineScope)
    }

    private fun defaultCallInit() {
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

        waitingLobbyOverlayViewModel.init(state.callState.callingStatus)
        connectingLobbyOverlayViewModel.init(
            state.callState.callingStatus,
            state.permissionState
        )
        holdOverlayViewModel.init(state.callState.callingStatus, state.audioSessionState.audioFocusStatus)

        participantGridViewModel.init(state.callState.callingStatus)
    }

    override suspend fun onStateChange(state: ReduxState) {

        if (state.callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN) {

            if (state.permissionState.audioPermissionState == PermissionStatus.GRANTED) {

                if (!track && state.callState.callingStatus == CallingStatus.NONE) {
                    runBlocking {
                        withContext(singleThreadedContext) {
                            track = true
                            dispatchAction(action = CallingAction.CallStartRequested())
                        }
                    }
                }
                defaultCallingStateChange(state)

            } else if (state.permissionState.audioPermissionState == PermissionStatus.DENIED) {
                exitComposite()
            } else {
                Log.d("Mohtasim", "onStateChange:: Case for ambigiuos permission state")
            }
        } else {
            defaultCallingStateChange(state)
        }

    }

    private fun defaultCallingStateChange(state: ReduxState) {

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

    private fun hasSkippedSetupScreenWithAudioGranted(state: ReduxState) = (
                state.callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN &&
                    state.permissionState.audioPermissionState == PermissionStatus.GRANTED
            )

}
