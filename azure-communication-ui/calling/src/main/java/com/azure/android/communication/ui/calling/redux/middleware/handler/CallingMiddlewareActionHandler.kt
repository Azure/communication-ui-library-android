// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.handler

import android.telecom.CallAudioState
import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.FatalError
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionMode
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilitiesChangedNotificationMode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.calling.models.CallDiagnosticModel
import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.buildCallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.into
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.AudioSessionAction
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.action.CallDiagnosticsAction
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.action.ToastNotificationAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsError
import com.azure.android.communication.ui.calling.redux.state.CaptionsErrorType
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationKind
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
    fun admit(userIdentifier: String, store: Store<ReduxState>)
    fun reject(userIdentifier: String, store: Store<ReduxState>)
    fun onAudioDeviceChangeRequested(requestedAudioDevice: AudioDeviceSelectionStatus, store: Store<ReduxState>)
    fun onAudioDeviceChangeSucceeded(selectedAudioDevice: AudioDeviceSelectionStatus, store: Store<ReduxState>)
    fun onAudioFocusRequesting(store: Store<ReduxState>)
    fun removeParticipant(userIdentifier: String, store: Store<ReduxState>)
    fun setCapabilities(capabilities: Set<ParticipantCapabilityType>, store: Store<ReduxState>)
    fun onNetworkQualityCallDiagnosticsUpdated(
        model: CallDiagnosticModel<NetworkCallDiagnostic, CallDiagnosticQuality>,
        store: Store<ReduxState>
    )
    fun onNetworkCallDiagnosticsUpdated(
        model: CallDiagnosticModel<NetworkCallDiagnostic, Boolean>,
        store: Store<ReduxState>
    )
    fun onMediaCallDiagnosticsUpdated(
        model: CallDiagnosticModel<MediaCallDiagnostic, Boolean>,
        store: Store<ReduxState>
    )
    fun dismissNotification(store: Store<ReduxState>)
    fun startCaptions(language: String, store: Store<ReduxState>)
    fun stopCaptions(store: Store<ReduxState>)
    fun setCaptionsSpokenLanguage(language: String, store: Store<ReduxState>)
    fun setCaptionsCaptionLanguage(language: String, store: Store<ReduxState>)
}

internal class CallingMiddlewareActionHandlerImpl(
    private val callingService: CallingService,
    coroutineContextProvider: CoroutineContextProvider,
    private val configuration: CallCompositeConfiguration,
    private val capabilitiesManager: CapabilitiesManager,
    private val localOptions: CallCompositeLocalOptions? = null
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
                                CallCompositeError(ErrorCode.TURN_CAMERA_OFF_FAILED, error)
                            )
                        )
                    } else {
                        store.dispatch(LocalParticipantAction.CameraPauseSucceeded())
                    }
                }
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
                    ParticipantAction.LobbyError(lobbyErrorCode)
                )
            }
        }
    }

    override fun admit(userIdentifier: String, store: Store<ReduxState>) {
        callingService.admit(userIdentifier).whenComplete { lobbyErrorCode, _ ->
            if (lobbyErrorCode != null) {
                store.dispatch(
                    ParticipantAction.LobbyError(lobbyErrorCode)
                )
            }
        }
    }

    override fun reject(userIdentifier: String, store: Store<ReduxState>) {
        callingService.reject(userIdentifier).whenComplete { lobbyErrorCode, _ ->
            if (lobbyErrorCode != null) {
                store.dispatch(
                    ParticipantAction.LobbyError(lobbyErrorCode)
                )
            }
        }
    }

    override fun removeParticipant(userIdentifier: String, store: Store<ReduxState>) {
        callingService.removeParticipant(userIdentifier).whenComplete { _, error ->
            if (error != null) {
                store.dispatch(
                    ParticipantAction.RemoveParticipantError()
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
                            FatalError(error, ErrorCode.CALL_END_FAILED)
                        )
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
            if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.NOT_ASKED)
                PermissionAction.CameraPermissionRequested()
            else if (store.getCurrentState().permissionState.cameraPermissionState == PermissionStatus.DENIED)
                LocalParticipantAction.CameraOffTriggered()
            else LocalParticipantAction.CameraPreviewOnTriggered()

        store.dispatch(action)
    }

    override fun turnCameraPreviewOn(store: Store<ReduxState>) {
        callingService.turnLocalCameraOn().handle { newVideoStreamId, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    LocalParticipantAction.CameraPreviewOnFailed(
                        CallCompositeError(ErrorCode.TURN_CAMERA_ON_FAILED, error)
                    )
                )
            } else {
                store.dispatch(LocalParticipantAction.CameraPreviewOnSucceeded(newVideoStreamId))
            }
        }

        callingService.turnLocalCameraOn()
    }

    override fun turnCameraOff(store: Store<ReduxState>) {
        callingService.turnCameraOff().whenComplete { _, error ->
            if (error != null) {
                store.dispatch(
                    LocalParticipantAction.CameraOffFailed(
                        CallCompositeError(ErrorCode.TURN_CAMERA_OFF_FAILED, error)
                    )
                )
            } else {
                store.dispatch(LocalParticipantAction.CameraOffSucceeded())
            }
        }
    }

    override fun setupCall(store: Store<ReduxState>) {
        callingService.setupCall().handle { _, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    ErrorAction.FatalErrorOccurred(
                        FatalError(error, ErrorCode.CAMERA_INIT_FAILED)
                    )
                )
            } else {
                if (store.getCurrentState().localParticipantState.initialCallJoinState.skipSetupScreen) {
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
        subscribeOnTotalRemoteParticipantCountChanged(store)
        subscribeOnCapabilitiesChanged(store)
        subscribeToCaptionsUpdates(store)

        callingService.startCall(
            store.getCurrentState().localParticipantState.cameraState,
            store.getCurrentState().localParticipantState.audioState
        ).handle { _, error: Throwable? ->
            if (error != null) {
                store.dispatch(
                    ErrorAction.FatalErrorOccurred(
                        FatalError(error, ErrorCode.CALL_JOIN_FAILED)
                    )
                )
            }
            // set telecom manager audio route
            onAudioDeviceChangeRequested(
                store.getCurrentState().localParticipantState.audioState.device,
                store
            )
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
                            CallCompositeError(ErrorCode.TURN_CAMERA_ON_FAILED, error)
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
                        CallCompositeError(ErrorCode.SWITCH_CAMERA_FAILED, error)
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
                            CallCompositeError(ErrorCode.TURN_MIC_ON_FAILED, error)
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
                            CallCompositeError(ErrorCode.TURN_MIC_OFF_FAILED, error)
                        )
                    )
                }
            }
        }
    }

    override fun setCapabilities(capabilities: Set<ParticipantCapabilityType>, store: Store<ReduxState>) {

        val state = store.getCurrentState()

        if (!capabilitiesManager.hasCapability(capabilities, ParticipantCapabilityType.TURN_VIDEO_ON) &&
            state.localParticipantState.cameraState.operation != CameraOperationalStatus.OFF
        ) {
            store.dispatch(LocalParticipantAction.CameraOffTriggered())
        }

        if (!capabilitiesManager.hasCapability(capabilities, ParticipantCapabilityType.UNMUTE_MICROPHONE) &&
            state.localParticipantState.audioState.operation != AudioOperationalStatus.OFF
        ) {
            store.dispatch(LocalParticipantAction.MicOffTriggered())
        }
    }

    override fun onNetworkQualityCallDiagnosticsUpdated(
        model: CallDiagnosticModel<NetworkCallDiagnostic, CallDiagnosticQuality>,
        store: Store<ReduxState>
    ) {
        if (model.diagnosticValue == CallDiagnosticQuality.BAD ||
            model.diagnosticValue == CallDiagnosticQuality.POOR
        ) {
            val kind = when (model.diagnosticKind) {
                NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY -> {
                    ToastNotificationKind.NETWORK_RECEIVE_QUALITY
                }
                NetworkCallDiagnostic.NETWORK_SEND_QUALITY -> {
                    ToastNotificationKind.NETWORK_SEND_QUALITY
                }
                NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY -> {
                    ToastNotificationKind.NETWORK_RECONNECTION_QUALITY
                }
                else -> null
            }
            kind?.let {
                store.dispatch(
                    ToastNotificationAction.ShowNotification(kind)
                )
            }
        } else {
            store.dispatch(ToastNotificationAction.DismissNotification())
        }
    }

    override fun onNetworkCallDiagnosticsUpdated(
        model: CallDiagnosticModel<NetworkCallDiagnostic, Boolean>,
        store: Store<ReduxState>
    ) {
        val kind = when (model.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_UNAVAILABLE -> ToastNotificationKind.NETWORK_UNAVAILABLE
            NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE -> ToastNotificationKind.NETWORK_RELAYS_UNREACHABLE

            else -> null
        }
        kind?.let {
            if (model.diagnosticValue) {
                store.dispatch(
                    ToastNotificationAction.ShowNotification(
                        kind
                    )
                )
            }
        }
    }

    override fun onAudioDeviceChangeRequested(
        requestedAudioDevice: AudioDeviceSelectionStatus,
        store: Store<ReduxState>
    ) {
        if (configuration.telecomManagerOptions != null) {
            // it TelecomManger integration is handled by SDK call setTelecomManagerAudioRoute
            if (configuration.telecomManagerOptions?.telecomManagerIntegrationMode == CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER) {
                val route = when (requestedAudioDevice) {
                    AudioDeviceSelectionStatus.SPEAKER_REQUESTED -> CallAudioState.ROUTE_SPEAKER
                    AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> CallAudioState.ROUTE_EARPIECE
                    AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> CallAudioState.ROUTE_BLUETOOTH
                    else -> return
                }
                callingService.setTelecomManagerAudioRoute(route)
            }
        }
    }

    override fun onAudioDeviceChangeSucceeded(
        selectedAudioDevice: AudioDeviceSelectionStatus,
        store: Store<ReduxState>
    ) {
        configuration.callCompositeEventsHandler.getOnAudioSelectionChangedEventHandlers().forEach {
            try {
                val audioSelectionType = when (selectedAudioDevice) {
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED -> CallCompositeAudioSelectionMode.SPEAKER
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED -> CallCompositeAudioSelectionMode.RECEIVER
                    AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> CallCompositeAudioSelectionMode.BLUETOOTH
                    else -> return
                }
                val event = buildCallCompositeAudioSelectionChangedEvent(audioSelectionType)
                it.handle(event)
            } catch (ex: Exception) {
                // catching and suppressing any client's exceptions
            }
        }
    }

    override fun onMediaCallDiagnosticsUpdated(
        model: CallDiagnosticModel<MediaCallDiagnostic, Boolean>,
        store: Store<ReduxState>
    ) {
        when (model.diagnosticKind) {
            MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED -> {
                if (model.diagnosticValue) {
                    store.dispatch(
                        ToastNotificationAction.ShowNotification(
                            ToastNotificationKind.SPEAKING_WHILE_MICROPHONE_IS_MUTED
                        )
                    )
                } else {
                    store.dispatch(ToastNotificationAction.DismissNotification())
                }
            }
            MediaCallDiagnostic.CAMERA_START_FAILED -> {
                if (model.diagnosticValue) {
                    store.dispatch(
                        ToastNotificationAction.ShowNotification(
                            ToastNotificationKind.CAMERA_START_FAILED
                        )
                    )
                }
            }
            MediaCallDiagnostic.CAMERA_START_TIMED_OUT -> {
                if (model.diagnosticValue) {
                    store.dispatch(
                        ToastNotificationAction.ShowNotification(
                            ToastNotificationKind.CAMERA_START_TIMED_OUT
                        )
                    )
                }
            }
            else -> {}
        }
    }

    override fun dismissNotification(store: Store<ReduxState>) {
        store.getCurrentState().toastNotificationState.kind?.let { kind ->

            if (kind == ToastNotificationKind.NETWORK_UNAVAILABLE) {
                val model = NetworkCallDiagnosticModel(
                    NetworkCallDiagnostic.NETWORK_UNAVAILABLE,
                    false
                )
                store.dispatch(CallDiagnosticsAction.NetworkCallDiagnosticsDismissed(model))
            }
            if (kind == ToastNotificationKind.NETWORK_RELAYS_UNREACHABLE) {
                val model = NetworkCallDiagnosticModel(
                    NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE,
                    false
                )
                store.dispatch(CallDiagnosticsAction.NetworkCallDiagnosticsDismissed(model))
            }
            if (kind == ToastNotificationKind.NETWORK_RECEIVE_QUALITY ||
                kind == ToastNotificationKind.NETWORK_RECONNECTION_QUALITY ||
                kind == ToastNotificationKind.NETWORK_SEND_QUALITY
            ) {
                val diagnostic = when (kind) {
                    ToastNotificationKind.NETWORK_RECEIVE_QUALITY -> NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY
                    ToastNotificationKind.NETWORK_RECONNECTION_QUALITY -> NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY
                    ToastNotificationKind.NETWORK_SEND_QUALITY -> NetworkCallDiagnostic.NETWORK_SEND_QUALITY
                    else -> null
                }
                diagnostic?.let {
                    val model = NetworkQualityCallDiagnosticModel(
                        diagnostic,
                        CallDiagnosticQuality.UNKNOWN
                    )
                    store.dispatch(
                        CallDiagnosticsAction.NetworkQualityCallDiagnosticsDismissed(
                            model
                        )
                    )
                }
            }

            if (kind == ToastNotificationKind.SPEAKING_WHILE_MICROPHONE_IS_MUTED ||
                kind == ToastNotificationKind.CAMERA_START_FAILED ||
                kind == ToastNotificationKind.CAMERA_START_TIMED_OUT
            ) {

                val mediaCallDiagnostic = when (kind) {
                    ToastNotificationKind.SPEAKING_WHILE_MICROPHONE_IS_MUTED -> MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED
                    ToastNotificationKind.CAMERA_START_FAILED -> MediaCallDiagnostic.CAMERA_START_FAILED
                    ToastNotificationKind.CAMERA_START_TIMED_OUT -> MediaCallDiagnostic.CAMERA_START_TIMED_OUT
                    else -> null
                }

                mediaCallDiagnostic?.let {
                    val model = MediaCallDiagnosticModel(mediaCallDiagnostic, false)
                    store.dispatch(CallDiagnosticsAction.MediaCallDiagnosticsDismissed(model))
                }
            }
        }
    }

    override fun onAudioFocusRequesting(store: Store<ReduxState>) {
        if (configuration.telecomManagerOptions != null) {
            store.dispatch(AudioSessionAction.AudioFocusApproved())
        }
    }

    override fun startCaptions(language: String, store: Store<ReduxState>) {
        callingService.startCaptions(language)
            .handle { _, error: Throwable? ->
                if (error != null) {
                    (error.cause as CallingCommunicationException).let {
                        store.dispatch(
                            CaptionsAction.Error(CaptionsError(it.errorCode.into(), CaptionsErrorType.CAPTIONS_START_ERROR))
                        )
                    }
                    store.dispatch(CaptionsAction.Stopped())
                } else {
                    store.dispatch(CaptionsAction.Started())
                }
            }
    }

    override fun stopCaptions(store: Store<ReduxState>) {
        callingService.stopCaptions()
            .handle { _, error: Throwable? ->
                if (error != null) {
                    (error.cause as CallingCommunicationException).let {
                        store.dispatch(
                            CaptionsAction.Error(CaptionsError(it.errorCode.into(), CaptionsErrorType.CAPTIONS_STOP_ERROR))
                        )
                    }
                } else {
                    store.dispatch(CaptionsAction.Stopped())
                }
            }
    }

    override fun setCaptionsSpokenLanguage(language: String, store: Store<ReduxState>) {
        callingService.setCaptionsSpokenLanguage(language)
            .handle { _, error: Throwable? ->
                if (error != null) {
                    (error.cause as CallingCommunicationException).let {
                        store.dispatch(
                            CaptionsAction.Error(CaptionsError(it.errorCode.into(), CaptionsErrorType.CAPTIONS_SPOKEN_LANGUAGE_UPDATE_ERROR))
                        )
                    }
                }
            }
    }

    override fun setCaptionsCaptionLanguage(language: String, store: Store<ReduxState>) {
        callingService.setCaptionsCaptionLanguage(language)
            .handle { _, error: Throwable? ->
                if (error != null) {
                    (error.cause as CallingCommunicationException).let {
                        store.dispatch(
                            CaptionsAction.Error(CaptionsError(it.errorCode.into(), CaptionsErrorType.CAPTIONS_CAPTION_LANGUAGE_UPDATE_ERROR))
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

    private fun subscribeDominantSpeakersUpdate(
        store: Store<ReduxState>,
    ) {
        coroutineScope.launch {
            callingService.getDominantSpeakersSharedFlow().collect {
                if (isActive) {
                    store.dispatch(ParticipantAction.DominantSpeakersUpdated(it))
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

    private fun subscribeOnLocalParticipantRoleChanged(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getLocalParticipantRoleSharedFlow().collect {
                val action = LocalParticipantAction.RoleChanged(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeOnTotalRemoteParticipantCountChanged(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getTotalRemoteParticipantCountSharedFlow().collect {
                val action = ParticipantAction.SetTotalParticipantCount(it)
                store.dispatch(action)
            }
        }
    }

    private fun subscribeOnCapabilitiesChanged(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getCapabilitiesChangedEventSharedFlow().collect { event ->
                // Set capabilities to the store
                val capabilities = callingService.getCallCapabilities()
                val action = LocalParticipantAction.SetCapabilities(capabilities)
                store.dispatch(action)

                if (configuration.capabilitiesChangedNotificationMode != CallCompositeCapabilitiesChangedNotificationMode.NEVER) {
                    // Only display toast message on capabilities changed
                    val anyLostCapability = event.changedCapabilities.any {
                        (it.type == ParticipantCapabilityType.UNMUTE_MICROPHONE && !it.isAllowed) ||
                            (it.type == ParticipantCapabilityType.TURN_VIDEO_ON && !it.isAllowed) ||
                            (it.type == ParticipantCapabilityType.MANAGE_LOBBY && !it.isAllowed)
                    }

                    store.dispatch(
                        ToastNotificationAction.ShowNotification(
                            if (anyLostCapability) ToastNotificationKind.SOME_FEATURES_LOST else ToastNotificationKind.SOME_FEATURES_GAINED
                        )
                    )
                }
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
                val state = store.getCurrentState()

                store.dispatch(CallingAction.StateUpdated(callInfoModel.callingStatus, callInfoModel.callEndReasonCode, callInfoModel.callEndReasonSubCode))

                callInfoModel.callStateError?.let {
                    val action = ErrorAction.CallStateErrorOccurred(it)
                    store.dispatch(action)
                    if (it.callCompositeEventCode == CallCompositeEventCode.CALL_EVICTED ||
                        it.callCompositeEventCode == CallCompositeEventCode.CALL_DECLINED
                    ) {
                        if (state.localParticipantState.initialCallJoinState.skipSetupScreen) {
                            store.dispatch(NavigationAction.Exit())
                        } else {
                            store.dispatch(CaptionsAction.Stopped())
                            store.dispatch(CaptionsAction.ClearError())
                            store.dispatch(NavigationAction.SetupLaunched())
                        }
                    } else if (it.errorCode == ErrorCode.CALL_END_FAILED ||
                        it.errorCode == ErrorCode.CALL_JOIN_FAILED
                    ) {
                        store.dispatch(CallingAction.IsTranscribingUpdated(false))
                        store.dispatch(CallingAction.IsRecordingUpdated(false))
                        store.dispatch(ParticipantAction.ListUpdated(HashMap()))
                        store.dispatch(CallingAction.StateUpdated(CallingStatus.NONE))
                        store.dispatch(CaptionsAction.Stopped())
                        store.dispatch(CaptionsAction.ClearError())
                        if (store.getCurrentState().localParticipantState.initialCallJoinState.skipSetupScreen) {
                            store.dispatch(NavigationAction.Exit())
                        } else {
                            store.dispatch(NavigationAction.SetupLaunched())
                        }
                    }
                }

                if (callInfoModel.callStateError == null) {
                    val action: NavigationAction? = when (callInfoModel.callingStatus) {
                        CallingStatus.DISCONNECTED -> NavigationAction.Exit()
                        CallingStatus.CONNECTED -> NavigationAction.CallLaunched()
                        CallingStatus.CONNECTING -> {
                            if (configuration.callConfig.callType == CallType.ONE_TO_N_OUTGOING) NavigationAction.CallLaunched() else null
                        }
                        CallingStatus.IN_LOBBY -> NavigationAction.CallLaunched()
                        else -> null
                    }
                    action?.let { store.dispatch(it) }

                    if (localOptions?.captionsOptions?.captionsOn == true &&
                        callInfoModel.callingStatus == CallingStatus.CONNECTED
                    ) {
                        store.dispatch(CaptionsAction.StartRequested(localOptions.captionsOptions?.spokenLanguage ?: ""))
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
                                CallCompositeError(ErrorCode.TURN_CAMERA_ON_FAILED, error)
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

    private fun subscribeToCaptionsUpdates(store: Store<ReduxState>) {
        coroutineScope.launch {
            callingService.getActiveCaptionLanguageChangedSharedFlow().collect {
                store.dispatch(CaptionsAction.CaptionLanguageChanged(it))
            }
        }

        coroutineScope.launch {
            callingService.getActiveSpokenLanguageChangedSharedFlow().collect {
                store.dispatch(CaptionsAction.SpokenLanguageChanged(it))
            }
        }

        coroutineScope.launch {
            callingService.getCaptionsTypeChangedSharedFlow().collect {
                store.dispatch(CaptionsAction.TypeChanged(it))
            }
        }

        coroutineScope.launch {
            callingService.getIsCaptionsTranslationSupportedSharedFlow().collect {
                store.dispatch(CaptionsAction.IsTranslationSupportedChanged(it))
            }
        }

        coroutineScope.launch {
            callingService.getCaptionsSupportedCaptionLanguagesSharedFlow().collect {
                store.dispatch(CaptionsAction.SupportedCaptionLanguagesChanged(it))
            }
        }

        coroutineScope.launch {
            callingService.getCaptionsSupportedSpokenLanguagesSharedFlow().collect {
                store.dispatch(CaptionsAction.SupportedSpokenLanguagesChanged(it))
            }
        }
    }
}
