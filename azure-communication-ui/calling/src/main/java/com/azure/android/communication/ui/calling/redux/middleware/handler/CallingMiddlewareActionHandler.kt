// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.handler

import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.FatalError
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CallDiagnosticsAction
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal interface CallingMiddlewareActionHandler {
    fun enterBackground(store: Store<ReduxState>)

    fun enterForeground(store: Store<ReduxState>)

    fun hold(store: Store<ReduxState>)

    fun resume(store: Store<ReduxState>)

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

    fun callSetupWithSkipSetupScreen(store: Store<ReduxState>)

    fun exit(store: Store<ReduxState>)

    fun dispose()

    fun admitAll(store: Store<ReduxState>)

    fun admit(
        userIdentifier: String,
        store: Store<ReduxState>,
    )

    fun decline(
        userIdentifier: String,
        store: Store<ReduxState>,
    )
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
            if (state.callState.callingStatus != CallingStatus.NONE &&
                state.callState.callingStatus != CallingStatus.LOCAL_HOLD
            ) {
                callingService.turnCameraOff().whenComplete { _, error ->
                    if (error != null) {
                        store.dispatch(
                            LocalParticipantAction.CameraPauseFailed(
                                CallCompositeError(ErrorCode.TURN_CAMERA_OFF_FAILED, error),
                            ),
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

        // turning camera on during hold state cause remote users to not see video
        // this check will make sure that if call is on hold, camera state is still paused
        // on resume call this logic will be retried
        val state = store.getCurrentState()
        if (state.callState.callingStatus != CallingStatus.LOCAL_HOLD) {
            tryCameraOn(store)
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

    override fun callSetupWithSkipSetupScreen(store: Store<ReduxState>) {
        if (store.getCurrentState().localParticipantState.initialCallJoinState.startWithMicrophoneOn) {
            store.dispatch(action = LocalParticipantAction.MicPreviewOnTriggered())
        }

        if (store.getCurrentState().localParticipantState.initialCallJoinState.startWithCameraOn) {
            store.dispatch(action = LocalParticipantAction.CameraPreviewOnRequested())
        }

        store.dispatch(action = CallingAction.SetupCall())
    }

    override fun dispose() {
        coroutineScope.cancel()
        callingService.dispose()
    }

    override fun admitAll(store: Store<ReduxState>) {
        callingService.admitAll().whenComplete { lobbyErrorCode, _ ->
            if (lobbyErrorCode != null) {
                store.dispatch(
                    ParticipantAction.LobbyError(lobbyErrorCode),
                )
            }
        }
    }

    override fun admit(
        userIdentifier: String,
        store: Store<ReduxState>,
    ) {
        callingService.admit(userIdentifier).whenComplete { lobbyErrorCode, _ ->
            if (lobbyErrorCode != null) {
                store.dispatch(
                    ParticipantAction.LobbyError(lobbyErrorCode),
                )
            }
        }
    }

    override fun decline(
        userIdentifier: String,
        store: Store<ReduxState>,
    ) {
        callingService.decline(userIdentifier).whenComplete { lobbyErrorCode, _ ->
            if (lobbyErrorCode != null) {
                store.dispatch(
                    ParticipantAction.LobbyError(lobbyErrorCode),
                )
            }
        }
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
                            FatalError(error, ErrorCode.CALL_END_FAILED),
                        ),
                    )
                }
            }
    }

    override fun hold(store: Store<ReduxState>) {
        callingService.hold()
            .handle { _, error: Throwable? ->
                if (error != null) {
                }
            }
    }

    override fun resume(store: Store<ReduxState>) {
        callingService.resume()
            .handle { _, error: Throwable? ->
                if (error != null) {
                }
            }
    }

    override fun requestCameraPreviewOn(store: Store<ReduxState>) {
        val action =
            if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.NOT_ASKED) {
                PermissionAction.CameraPermissionRequested()
            } else if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.DENIED) {
                LocalParticipantAction.CameraOffTriggered()
            } else {
                LocalParticipantAction.CameraPreviewOnTriggered()
            }

        store.dispatch(action)
    }

    override fun turnCameraPreviewOn(store: Store<ReduxState>) {
        callingService.turnLocalCameraOn().handle { newVideoStreamId, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    LocalParticipantAction.CameraPreviewOnFailed(
                        CallCompositeError(ErrorCode.TURN_CAMERA_ON_FAILED, error),
                    ),
                )
            } else {
                store.dispatch(LocalParticipantAction.CameraPreviewOnSucceeded(newVideoStreamId))
            }
        }

        callingService.turnLocalCameraOn()
    }

    override fun turnCameraOff(store: Store<ReduxState>) {
        if (store.getCurrentState().callState.callingStatus != CallingStatus.NONE) {
            callingService.turnCameraOff().whenComplete { _, error ->
                if (error != null) {
                    store.dispatch(
                        LocalParticipantAction.CameraOffFailed(
                            CallCompositeError(ErrorCode.TURN_CAMERA_OFF_FAILED, error),
                        ),
                    )
                } else {
                    store.dispatch(LocalParticipantAction.CameraOffSucceeded())
                }
            }
        }
    }

    override fun setupCall(store: Store<ReduxState>) {
        callingService.setupCall().handle { _, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    ErrorAction.FatalErrorOccurred(
                        FatalError(error, ErrorCode.CAMERA_INIT_FAILED),
                    ),
                )
            } else {
                if (store.getCurrentState().callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN) {
                    store.dispatch(action = CallingAction.CallStartRequested())
                }
            }
        }
    }

    override fun startCall(store: Store<ReduxState>) {
        subscribeRemoteParticipantsUpdate(store)
        subscribeIsMutedUpdate(store)
        subscribeIsRecordingUpdate(store)
        subscribeIsTranscribingUpdate(store)
        subscribeToUserFacingDiagnosticsUpdates(store)
        subscribeCallInfoModelEventUpdate(store)
        subscribeCallIdUpdate(store)
        subscribeCamerasCountUpdate(store)
        subscribeDominantSpeakersUpdate(store)
        subscribeOnLocalParticipantRoleChanged(store)

        callingService.startCall(
            store.getCurrentState().localParticipantState.cameraState,
            store.getCurrentState().localParticipantState.audioState,
        ).handle { _, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    ErrorAction.FatalErrorOccurred(
                        FatalError(error, ErrorCode.CALL_JOIN_FAILED),
                    ),
                )
            }
        }
    }

    override fun requestCameraOn(store: Store<ReduxState>) {
        val action =
            if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.NOT_ASKED) {
                PermissionAction.CameraPermissionRequested()
            } else {
                LocalParticipantAction.CameraOnTriggered()
            }

        store.dispatch(action)
    }

    override fun turnCameraOn(store: Store<ReduxState>) {
        if (store.getCurrentState().callState.callingStatus != CallingStatus.NONE) {
            callingService.turnCameraOn().handle { newVideoStreamId, error: Throwable? ->
                if (error != null) {
                    store.dispatch(
                        LocalParticipantAction.CameraOnFailed(
                            CallCompositeError(ErrorCode.TURN_CAMERA_ON_FAILED, error),
                        ),
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
                        CallCompositeError(ErrorCode.SWITCH_CAMERA_FAILED, error),
                    ),
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
                            CallCompositeError(ErrorCode.TURN_MIC_ON_FAILED, error),
                        ),
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
                            CallCompositeError(ErrorCode.TURN_MIC_OFF_FAILED, error),
                        ),
                    )
                }
            }
        }
    }

    private fun subscribeCamerasCountUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getCamerasCountStateFlow().collect {
                store.dispatch(LocalParticipantAction.CamerasCountUpdated(it))
            }
        }
    }

    private fun subscribeIsMutedUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getIsMutedSharedFlow().collect {
                val action =
                    if (it) {
                        LocalParticipantAction.AudioStateOperationUpdated(AudioOperationalStatus.OFF)
                    } else {
                        LocalParticipantAction.AudioStateOperationUpdated(AudioOperationalStatus.ON)
                    }
                store.dispatch(action)
            }
        }
    }

    private fun subscribeRemoteParticipantsUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getParticipantsInfoModelSharedFlow().collect {
                if (isActive) {
                    val participantUpdateAction = ParticipantAction.ListUpdated(HashMap(it))
                    store.dispatch(participantUpdateAction)
                }
            }
        }
    }

    private fun subscribeDominantSpeakersUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getDominantSpeakersSharedFlow()?.collect {
                if (isActive) {
                    store.dispatch(ParticipantAction.DominantSpeakersUpdated(it))
                }
            }
        }
    }

    private fun subscribeIsRecordingUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getIsRecordingSharedFlow()?.collect {
                val action = CallingAction.IsRecordingUpdated(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeOnLocalParticipantRoleChanged(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getLocalParticipantRoleSharedFlow()?.collect {
                val action = LocalParticipantAction.RoleChanged(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeIsTranscribingUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getIsTranscribingSharedFlow()?.collect {
                val action = CallingAction.IsTranscribingUpdated(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeToUserFacingDiagnosticsUpdates(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getNetworkQualityCallDiagnosticsFlow().collect {
                val action = CallDiagnosticsAction.NetworkQualityCallDiagnosticsUpdated(it)
                store.dispatch(action)
            }
        }

        coroutineScope.launch {
            callingService.getNetworkCallDiagnosticsFlow().collect {
                val action = CallDiagnosticsAction.NetworkCallDiagnosticsUpdated(it)
                store.dispatch(action)
            }
        }

        coroutineScope.launch {
            callingService.getMediaCallDiagnosticsFlow().collect {
                val action = CallDiagnosticsAction.MediaCallDiagnosticsUpdated(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeCallInfoModelEventUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getCallInfoModelEventSharedFlow().collect { callInfoModel ->
                val previousCallState = store.getCurrentState().callState.callingStatus

                store.dispatch(CallingAction.StateUpdated(callInfoModel.callingStatus))

                if (previousCallState == CallingStatus.LOCAL_HOLD &&
                    callInfoModel.callingStatus == CallingStatus.CONNECTED
                ) {
                    tryCameraOn(store)
                }

                if (store.getCurrentState().callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN &&
                    callInfoModel.callingStatus == CallingStatus.CONNECTED
                ) {
                    tryCameraOn(store)
                }

                callInfoModel.callStateError?.let {
                    val action = ErrorAction.CallStateErrorOccurred(it)
                    store.dispatch(action)
                    if (it.callCompositeEventCode == CallCompositeEventCode.CALL_EVICTED ||
                        it.callCompositeEventCode == CallCompositeEventCode.CALL_DECLINED
                    ) {
                        if (store.getCurrentState().callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN) {
                            store.dispatch(NavigationAction.Exit())
                        } else {
                            store.dispatch(NavigationAction.SetupLaunched())
                        }
                    } else if (it.errorCode == ErrorCode.CALL_END_FAILED ||
                        it.errorCode == ErrorCode.CALL_JOIN_FAILED
                    ) {
                        store.dispatch(CallingAction.IsTranscribingUpdated(false))
                        store.dispatch(CallingAction.IsRecordingUpdated(false))
                        store.dispatch(ParticipantAction.ListUpdated(HashMap()))
                        store.dispatch(CallingAction.StateUpdated(CallingStatus.NONE))
                        if (store.getCurrentState().callState.operationStatus == OperationStatus.SKIP_SETUP_SCREEN) {
                            store.dispatch(NavigationAction.Exit())
                        } else {
                            store.dispatch(NavigationAction.SetupLaunched())
                        }
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
                        else -> {}
                    }
                }
            }
        }
    }

    private fun subscribeCallIdUpdate(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getCallIdStateFlow().collect {
                store.dispatch(CallingAction.CallIdUpdated(it))
            }
        }
    }

    private fun tryCameraOn(store: Store<ReduxState>) {
        val state = store.getCurrentState()
        if (state.localParticipantState.cameraState.operation == CameraOperationalStatus.PAUSED ||
            state.localParticipantState.cameraState.operation == CameraOperationalStatus.PENDING
        ) {
            if (state.callState.callingStatus != CallingStatus.NONE) {
                callingService.turnCameraOn().handle { newVideoStreamId, error: Throwable? ->
                    if (error != null) {
                        store.dispatch(
                            LocalParticipantAction.CameraPauseFailed(
                                CallCompositeError(ErrorCode.TURN_CAMERA_ON_FAILED, error),
                            ),
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
}
