// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.middleware.handler

import com.azure.android.communication.ui.configuration.events.CommunicationUIErrorCode
import com.azure.android.communication.ui.error.CallCompositeError
import com.azure.android.communication.ui.error.FatalError
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.ErrorAction
import com.azure.android.communication.ui.redux.action.LifecycleAction
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.NavigationAction
import com.azure.android.communication.ui.redux.action.ParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.CallingService
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal interface CallingMiddlewareActionHandler {
    fun enterBackground(store: Store<ReduxState>)
    fun enterForeground(store: Store<ReduxState>)
    fun endCall(store: Store<ReduxState>)
    fun requestCameraPreviewOn(store: Store<ReduxState>)
    fun turnCameraPreviewOn(store: Store<ReduxState>)
    fun requestCameraOn(store: Store<ReduxState>)
    fun turnCameraOn(store: Store<ReduxState>)
    fun turnCameraOff(store: Store<ReduxState>)
    fun switchCamera(store: Store<ReduxState>)
    fun setupCall(store: Store<ReduxState>)
    fun startCall(store: Store<ReduxState>)
    fun turnMicOn(store: Store<ReduxState>)
    fun turnMicOff(store: Store<ReduxState>)
    fun onCameraPermissionIsSet(store: Store<ReduxState>)
    fun exit(store: Store<ReduxState>)
    fun dispose()
}

internal class CallingMiddlewareActionHandlerImpl(
    private val callingService: CallingService,
    coroutineContextProvider: CoroutineContextProvider,
) :
    CallingMiddlewareActionHandler {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    override fun enterBackground(store: Store<ReduxState>) {
        store.dispatch(LifecycleAction.EnterBackgroundSucceeded())
        val state = store.getCurrentState()

        if (state.localParticipantState.cameraState.operation != CameraOperationalStatus.OFF &&
            state.localParticipantState.cameraState.operation != CameraOperationalStatus.PAUSED
        ) {
            if (state.callState.callingStatus != CallingStatus.NONE) {
                callingService.turnCameraOff().whenComplete { _, error ->
                    if (error != null) {
                        store.dispatch(
                            LocalParticipantAction.CameraPauseFailed(
                                CallCompositeError(CommunicationUIErrorCode.TURN_CAMERA_OFF, error)
                            )
                        )
                    } else {
                        store.dispatch(LocalParticipantAction.CameraPauseSucceeded())
                    }
                }
            } else {
                store.dispatch(LocalParticipantAction.CameraPauseSucceeded())
            }
        }
    }

    override fun enterForeground(store: Store<ReduxState>) {
        store.dispatch(LifecycleAction.EnterForegroundSucceeded())
        val state = store.getCurrentState()
        if (state.localParticipantState.cameraState.operation == CameraOperationalStatus.PAUSED) {
            if (state.callState.callingStatus != CallingStatus.NONE) {
                callingService.turnCameraOn().handle { newVideoStreamId, error: Throwable? ->
                    if (error != null) {
                        store.dispatch(
                            LocalParticipantAction.CameraPauseFailed(
                                CallCompositeError(CommunicationUIErrorCode.TURN_CAMERA_ON, error)
                            )
                        )
                    } else {
                        store.dispatch(LocalParticipantAction.CameraOnSucceeded(newVideoStreamId))
                    }
                }
            } else {
                store.dispatch(LocalParticipantAction.CameraPreviewOnTriggered())
            }
        }
    }

    override fun onCameraPermissionIsSet(store: Store<ReduxState>) {
        val state = store.getCurrentState()
        if ((state.permissionState.cameraPermissionState == PermissionStatus.GRANTED) &&
            (state.localParticipantState.cameraState.operation == CameraOperationalStatus.PENDING)
        ) {
            when (state.localParticipantState.cameraState.transmission) {
                CameraTransmissionStatus.LOCAL -> {
                    store.dispatch(LocalParticipantAction.CameraPreviewOnTriggered())
                }
                CameraTransmissionStatus.REMOTE -> {
                    store.dispatch(LocalParticipantAction.CameraOnTriggered())
                }
            }
        }
    }

    override fun dispose() {
        coroutineScope.cancel()
        callingService.dispose()
    }

    override fun exit(store: Store<ReduxState>) {
        store.dispatch(NavigationAction.Exit())
    }

    override fun endCall(store: Store<ReduxState>) {
        callingService.endCall()
            .handle { _, error: Throwable? ->
                if (error != null) {
                    store.dispatch(
                        ErrorAction.FatalErrorOccurred(
                            FatalError(error, CommunicationUIErrorCode.CALL_END)
                        )
                    )
                }
            }
    }

    override fun requestCameraPreviewOn(store: Store<ReduxState>) {
        val action =
            if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.NOT_ASKED)
                PermissionAction.CameraPermissionRequested() else LocalParticipantAction.CameraPreviewOnTriggered()

        store.dispatch(action)
    }

    override fun turnCameraPreviewOn(store: Store<ReduxState>) {
        callingService.turnLocalCameraOn().handle { newVideoStreamId, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    LocalParticipantAction.CameraPreviewOnFailed(
                        CallCompositeError(CommunicationUIErrorCode.TURN_CAMERA_ON, error)
                    )
                )
            } else {
                store.dispatch(LocalParticipantAction.CameraPreviewOnSucceeded(newVideoStreamId))
            }
        }
    }

    override fun turnCameraOff(store: Store<ReduxState>) {
        if (store.getCurrentState().callState.callingStatus != CallingStatus.NONE) {
            callingService.turnCameraOff().whenComplete { _, error ->
                if (error != null) {
                    store.dispatch(
                        LocalParticipantAction.CameraOffFailed(
                            CallCompositeError(CommunicationUIErrorCode.TURN_CAMERA_OFF, error)
                        )
                    )
                } else {
                    store.dispatch(LocalParticipantAction.CameraOffSucceeded())
                }
            }
        }
    }

    override fun setupCall(store: Store<ReduxState>) {
        callingService.setupCall()
    }

    override fun startCall(store: Store<ReduxState>) {
        subscribeRemoteParticipantsUpdate(store, coroutineScope)
        subscribeIsMutedUpdate(store)
        subscribeIsRecordingUpdate(store)
        subscribeIsTranscribingUpdate(store)
        subscribeCallInfoModelEventUpdate(store)

        callingService.startCall(
            store.getCurrentState().localParticipantState.cameraState,
            store.getCurrentState().localParticipantState.audioState
        ).handle { _, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    ErrorAction.FatalErrorOccurred(
                        FatalError(error, CommunicationUIErrorCode.CALL_JOIN)
                    )
                )
            }
        }
    }

    override fun requestCameraOn(store: Store<ReduxState>) {
        val action =
            if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.NOT_ASKED)
                PermissionAction.CameraPermissionRequested() else LocalParticipantAction.CameraOnTriggered()

        store.dispatch(action)
    }

    override fun turnCameraOn(store: Store<ReduxState>) {
        if (store.getCurrentState().callState.callingStatus != CallingStatus.NONE) {
            callingService.turnCameraOn().handle { newVideoStreamId, error: Throwable? ->
                if (error != null) {
                    store.dispatch(
                        LocalParticipantAction.CameraOnFailed(
                            CallCompositeError(CommunicationUIErrorCode.TURN_CAMERA_ON, error)
                        )
                    )
                } else {
                    store.dispatch(LocalParticipantAction.CameraOnSucceeded(newVideoStreamId))
                }
            }
        }
    }

    override fun switchCamera(store: Store<ReduxState>) {
        val currentCamera = store.getCurrentState().localParticipantState.cameraState.device

        callingService.switchCamera().handle { cameraDevice, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    LocalParticipantAction.CameraSwitchFailed(
                        currentCamera,
                        CallCompositeError(CommunicationUIErrorCode.SWITCH_CAMERA, error)
                    )
                )
            } else {
                store.dispatch(LocalParticipantAction.CameraSwitchSucceeded(cameraDevice))
            }
        }
    }

    override fun turnMicOn(store: Store<ReduxState>) {
        if (store.getCurrentState().localParticipantState.audioState.operation != AudioOperationalStatus.PENDING) {
            callingService.turnMicOn().whenComplete { _, error: Throwable? ->
                if (error != null) {
                    store.dispatch(
                        LocalParticipantAction.MicOnFailed(
                            CallCompositeError(CommunicationUIErrorCode.TURN_MIC_ON, error)
                        )
                    )
                }
            }
        }
    }

    override fun turnMicOff(store: Store<ReduxState>) {
        if (store.getCurrentState().localParticipantState.audioState.operation != AudioOperationalStatus.PENDING) {
            callingService.turnMicOff().whenComplete { _, error: Throwable? ->
                if (error != null) {
                    store.dispatch(
                        LocalParticipantAction.MicOffFailed(
                            CallCompositeError(CommunicationUIErrorCode.TURN_MIC_OFF, error)
                        )
                    )
                }
            }
        }
    }

    private fun subscribeIsMutedUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getIsMutedSharedFlow().collect {
                val action = if (it) {
                    LocalParticipantAction.AudioStateOperationUpdated(AudioOperationalStatus.OFF)
                } else {
                    LocalParticipantAction.AudioStateOperationUpdated(AudioOperationalStatus.ON)
                }
                store.dispatch(action)
            }
        }
    }

    private fun subscribeRemoteParticipantsUpdate(
        store: Store<ReduxState>,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            callingService.getParticipantsInfoModelSharedFlow().collect {
                if (isActive) {
                    val participantUpdateAction = ParticipantAction.ListUpdated(HashMap(it))
                    store.dispatch(participantUpdateAction)
                }
            }
        }
    }

    private fun subscribeIsRecordingUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getIsRecordingSharedFlow().collect {
                val action = CallingAction.IsRecordingUpdated(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeIsTranscribingUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getIsTranscribingSharedFlow().collect {
                val action = CallingAction.IsTranscribingUpdated(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeCallInfoModelEventUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getCallInfoModelEventSharedFlow().collect { callInfoModel ->
                store.dispatch(CallingAction.StateUpdated(callInfoModel.callingStatus))

                callInfoModel.callStateError?.let {
                    val action = ErrorAction.CallStateErrorOccurred(it)
                    store.dispatch(action)

                    if (it.communicationUIErrorCode == CommunicationUIErrorCode.CALL_END || it.communicationUIErrorCode == CommunicationUIErrorCode.CALL_JOIN) {
                        store.dispatch(CallingAction.IsTranscribingUpdated(false))
                        store.dispatch(CallingAction.IsRecordingUpdated(false))
                        store.dispatch(ParticipantAction.ListUpdated(HashMap()))
                        store.dispatch(CallingAction.StateUpdated(CallingStatus.NONE))
                        store.dispatch(NavigationAction.SetupLaunched())
                    }
                }

                if (callInfoModel.callStateError == null) {
                    when (callInfoModel.callingStatus) {
                        CallingStatus.CONNECTED, CallingStatus.IN_LOBBY -> {
                            store.dispatch(NavigationAction.CallLaunched())
                        }
                        CallingStatus.DISCONNECTED -> {
                            store.dispatch(NavigationAction.Exit())
                        }
                    }
                }
            }
        }
    }
}
