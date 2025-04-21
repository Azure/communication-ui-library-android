// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.handler

import android.telecom.CallAudioState
import com.azure.android.communication.calling.CallingCommunicationErrors
import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.configuration.CallConfiguration
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.CALL_END_FAILED
import com.azure.android.communication.ui.calling.helper.UnconfinedTestContextProvider
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode.Companion.CALL_DECLINED
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode.Companion.CALL_EVICTED
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerOptions
import com.azure.android.communication.ui.calling.models.CallInfoModel
import com.azure.android.communication.ui.calling.models.CapabilitiesChangedEvent
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.RttMessage
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.AudioSessionAction
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.action.RttAction
import com.azure.android.communication.ui.calling.redux.action.ToastNotificationAction
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RttState
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationKind
import com.azure.android.communication.ui.calling.service.CallingService
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.inOrder
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
/*  <CALL_START_TIME>
import java.util.Date
</CALL_START_TIME> */

@RunWith(MockitoJUnitRunner::class)
internal class CallingMiddlewareActionHandlerUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun callingMiddlewareActionHandler_turnCameraOff_when_navigationState_inCall_then_dispatchUpdateCameraStateToStore() {
        // arrange
        val cameraStateCompletableFuture = CompletableFuture<Void>()
        val appState = AppReduxState("", false, false)
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.turnCameraOff(mockAppStore)
        cameraStateCompletableFuture.complete(null)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOffSucceeded
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_turnCameraOff_fails_when_navigationState_inCall_then_dispatchUnableStopVideoError() {
        // arrange
        val cameraStateCompletableFuture = CompletableFuture<Void>()
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val error = Exception("test")

        // act
        handler.turnCameraOff(mockAppStore)
        cameraStateCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOffFailed && action.error.cause == error && action.error.errorCode == ErrorCode.TURN_CAMERA_OFF_FAILED
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_turnCameraOn_when_navigationState_inCall_then_dispatchRequestCameraOnToStore() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOn() } doReturn cameraStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.turnCameraOn(mockAppStore)
        cameraStateCompletableFuture.complete("1345")

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                (
                    action is LocalParticipantAction.CameraOnSucceeded &&
                        action.videoStreamID == "1345"
                    )
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_requestCameraPreview_when_cameraPermissionState_notAsked_then_dispatchRequestCameraPermissionToStore() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.permissionState = PermissionState(
            cameraPermissionState = PermissionStatus.NOT_ASKED,
            audioPermissionState = PermissionStatus.NOT_ASKED
        )

        val mockCallingService: CallingService = mock {}

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doAnswer { appState }
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.requestCameraPreviewOn(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is PermissionAction.CameraPermissionRequested
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_requestCameraPreview_when_cameraPermissionState_notAsked_then_dispatchTurnCameraPreviewOnStore() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.permissionState = PermissionState(
            cameraPermissionState = PermissionStatus.GRANTED,
            audioPermissionState = PermissionStatus.NOT_ASKED
        )

        val mockCallingService: CallingService = mock {}

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doAnswer { appState }
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.requestCameraPreviewOn(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPreviewOnTriggered
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_turnCameraPreviewOn_when_then_dispatchUpdateCameraStateOnToStore() {
        // arrange

        val cameraStateCompletableFuture: CompletableFuture<String> = CompletableFuture()

        val mockCallingService: CallingService = mock {
            on { turnLocalCameraOn() } doReturn cameraStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.turnCameraPreviewOn(mockAppStore)
        cameraStateCompletableFuture.complete("1345")

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPreviewOnSucceeded &&
                    action.videoStreamID == "1345"
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_requestCameraOn_when_permissionState_cameraPermissionState_notAsked_then_dispatchRequestCameraPermission() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.permissionState =
            PermissionState(PermissionStatus.GRANTED, PermissionStatus.NOT_ASKED)
        appState.navigationState = NavigationState(
            NavigationStatus.SETUP
        )
        appState.callState = CallingState(CallingStatus.NONE,)

        val mockCallingService: CallingService = mock {}

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.requestCameraOn(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is PermissionAction.CameraPermissionRequested
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_requestCameraOn_when_permissionState_cameraPermissionState_granted_then_dispatchRequestCameraPermission() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.permissionState =
            PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.GRANTED)
        appState.navigationState = NavigationState(
            NavigationStatus.SETUP
        )
        appState.callState = CallingState(CallingStatus.NONE,)

        val mockCallingService: CallingService = mock {}

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.requestCameraOn(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOnTriggered
            }
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_dispatchParticipantUpdateActionToStore() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE,
                        0
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
            participantMap["user"] =
                ParticipantInfoModel(
                    "user", "id",
                    isMuted = false,
                    isCameraDisabled = false,
                    isSpeaking = false,
                    isTypingRtt = false,
                    screenShareVideoStreamModel = null,
                    cameraVideoStreamModel = null,
                    modifiedTimestamp = 0,
                    participantStatus = ParticipantStatus.HOLD,
                )

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callingServiceParticipantsSharedFlow.emit(participantMap)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ParticipantAction.ListUpdated &&
                        action.participantMap == participantMap
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_dispatchSetAudioDevice_forTelecomManager() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE,
                        0
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_REQUESTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()

            val completableFuture = CompletableFuture<Void>()
            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn completableFuture
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { setTelecomManagerAudioRoute(CallAudioState.ROUTE_SPEAKER) } doAnswer {}
            }

            val configuration = CallCompositeConfiguration()
            configuration.telecomManagerOptions = CallCompositeTelecomManagerOptions(
                CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
                "com.example.telecom.TelecomManager",
            )

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                UnconfinedTestContextProvider(),
                configuration,
                CapabilitiesManager(CallType.GROUP_CALL)
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            completableFuture.complete(null)
            handler.startCall(mockAppStore)

            // assert
            verify(mockCallingService, times(1)).setTelecomManagerAudioRoute(CallAudioState.ROUTE_SPEAKER)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_dispatchDominantSpeakersUpdatedActionToStore() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE,
                        0
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val dominantSpeakers = listOf("userId")

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            dominantSpeakersSharedFlow.emit(dominantSpeakers)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ParticipantAction.DominantSpeakersUpdated &&
                        action.dominantSpeakersInfo == dominantSpeakers
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_dispatchCallStateUpdateActionToStore() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow: MutableSharedFlow<MutableMap<String, ParticipantInfoModel>> =
                MutableSharedFlow()

            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(CallInfoModel(CallingStatus.CONNECTED, null, callEndReasonSubCode = 123, callEndReasonCode = 456))

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.CONNECTED &&
                        action.callEndReasonSubCode == 123 &&
                        action.callEndReasonCode == 456
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_setupCall_fails_then_dispatchFatalError() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val setupCallCompletableFuture: CompletableFuture<Void> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { setupCall() } doReturn setupCallCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }

            val exception = Exception("test")
            handler.setupCall(mockAppStore)
            setupCallCompletableFuture.completeExceptionally(exception)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.FatalErrorOccurred &&
                        action.error.fatalError == exception && action.error.errorCode == ErrorCode.CAMERA_INIT_FAILED
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_fails_then_dispatchFatalError() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE,
                        2
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )

            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            val exception = Exception("test")

            // act
            handler.startCall(mockAppStore)
            startCallCompletableFuture.completeExceptionally(exception)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.FatalErrorOccurred &&
                        action.error.fatalError == exception && action.error.errorCode == ErrorCode.CALL_JOIN_FAILED
                }
            )
        }

//    @ExperimentalCoroutinesApi
//    @Test
//    fun callingMiddlewareActionHandler_startCall_cameraOff_micOff_then_useCorrectCameraAndAudioStates() =
//        runScopedTest {
//            // arrange
//            val expectedCameraState = CameraState(
//                CameraOperationalStatus.OFF,
//                CameraDeviceSelectionStatus.FRONT,
//                CameraTransmissionStatus.REMOTE
//            )
//            val expectedAudioState =
//                AudioState(
//                    AudioOperationalStatus.OFF,
//                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
//                    BluetoothState(available = false, deviceName = "bluetooth")
//                )
//            val appState = AppReduxState("", false, false)
//            appState.callState = CallingState(CallingStatus.CONNECTED,)
//            appState.localParticipantState =
//                LocalUserState(
//                    expectedCameraState,
//                    expectedAudioState,
//                    "",
//                    "",
//                    localParticipantRole = null
//                )
//            val startCallCompletableFuture = CompletableFuture<Void>()
//            val callingServiceParticipantsSharedFlow =
//                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
//            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
//            val callIdFlow = MutableStateFlow<String?>(null)
//            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
//            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
//            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
//            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
//            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
//            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
//            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
//            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
//            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
//            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
//            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
//            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
//            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
//            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
//            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
//
//            val mockCallingService: CallingService = mock {
//                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
//                on { startCall(any(), any()) } doReturn startCallCompletableFuture
//                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
//                on { getCallIdStateFlow() } doReturn callIdFlow
//                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
//                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
//                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
//                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
//                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
//                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
//                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
//            }
//
//            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
//
//            val mockAppStore = mock<AppStore<ReduxState>> {
//                on { dispatch(any()) } doAnswer { }
//                on { getCurrentState() } doAnswer { appState }
//            }
//
//            // act
//            handler.startCall(mockAppStore)
//            startCallCompletableFuture.complete(null)
//
//            // assert
//            verify(mockCallingService, times(1)).startCall(expectedCameraState, expectedAudioState)
//        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_cameraOff_micOn_then_useCorrectCameraAndAudioStates() =
        runScopedTest {
            // arrange
            val expectedCameraState = CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            )
            val expectedAudioState =
                AudioState(
                    AudioOperationalStatus.ON,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    "",
                    localParticipantRole = null
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            startCallCompletableFuture.complete(null)

            // assert
            verify(mockCallingService, times(1)).startCall(expectedCameraState, expectedAudioState)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_cameraOn_micOff_then_useCorrectCameraAndAudioStates() =
        runScopedTest {
            // arrange
            val expectedCameraState = CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            )
            val expectedAudioState =
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    "",
                    localParticipantRole = null
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            startCallCompletableFuture.complete(null)

            // assert
            verify(mockCallingService, times(1)).startCall(expectedCameraState, expectedAudioState)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_cameraOn_micOn_then_useCorrectCameraAndAudioStates() =
        runScopedTest {
            // arrange
            val expectedCameraState = CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            )
            val expectedAudioState =
                AudioState(
                    AudioOperationalStatus.ON,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    "",
                    localParticipantRole = null
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            startCallCompletableFuture.complete(null)

            // assert
            verify(mockCallingService, times(1)).startCall(expectedCameraState, expectedAudioState)
        }

    @Test
    fun callingMiddlewareActionHandler_enterBackground_then_dispatch_OnCameraStateChange_and_OnEnteredBackground() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState =
            LocalUserState(
                CameraState(
                    CameraOperationalStatus.ON,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.REMOTE
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                videoStreamID = null,
                displayName = "username",
                localParticipantRole = null
            )
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val cameraStateCompletableFuture = CompletableFuture<Void>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterBackground(mockAppStore)
        cameraStateCompletableFuture.complete(null)

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterBackgroundSucceeded })
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPauseSucceeded
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_enterBackground_then_doNothingIfCameraIsOff() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterBackground(mockAppStore)
        cameraStateCompletableFuture.complete(null)

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterBackgroundSucceeded })
        verify(
            mockAppStore,
            times(0)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraPauseSucceeded })
        verify(
            mockAppStore,
            times(0)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraPauseFailed })
    }

    @Test
    fun callingMiddlewareActionHandler_enterBackground_then_doNothingNotInCall() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.ON,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<Void>()
        cameraStateCompletableFuture.complete(null)

        val mockCallingService = mock<CallingService> {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterBackground(mockAppStore)

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterBackgroundSucceeded })
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraPauseSucceeded })

        verify(
            mockCallingService,
            times(1)
        ).turnCameraOff()
    }

    @Test
    fun callingMiddlewareActionHandler_enterBackground_then_doNothingAlreadyInBackground() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterBackground(mockAppStore)
        cameraStateCompletableFuture.complete("")

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterBackgroundSucceeded })
        verify(
            mockAppStore,
            times(0)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraOffTriggered })
    }

    @Test
    fun callingMiddlewareActionHandler_enterForeground_then_dispatch_OnCameraStateChange_and_OnEnteredForeground() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOn() } doReturn cameraStateCompletableFuture
        }
        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterForeground(mockAppStore)
        cameraStateCompletableFuture.complete("streamId")

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterForegroundSucceeded })

        verify(mockCallingService, times(1)).turnCameraOn()
    }

    @Test
    fun callingMiddlewareActionHandler_enterForeground_then_not_dispatch_OnCameraStateChange_if_stateIsLocalHold() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.LOCAL_HOLD,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock {}
        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterForeground(mockAppStore)
        cameraStateCompletableFuture.complete("streamId")

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterForegroundSucceeded })

        verify(mockCallingService, times(0)).turnCameraOn()
    }

    @Test
    fun callingMiddlewareActionHandler_enterForeground_then_dispatch_DoNotTurnCameraOnIfWasNotPaused() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterForeground(mockAppStore)
        cameraStateCompletableFuture.complete("streamId")

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterForegroundSucceeded })
        verify(
            mockAppStore,
            times(0)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraOnTriggered })
    }

    @Test
    fun callingMiddlewareActionHandler_enterForeground_then_dispatch_DoNotTurnCameraOnIfWasNotInCall() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )
        appState.callState = CallingState(CallingStatus.NONE,)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.enterForeground(mockAppStore)
        cameraStateCompletableFuture.complete("streamId")

        // assert
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LifecycleAction.EnterForegroundSucceeded })
        verify(
            mockAppStore,
            times(0)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraOnTriggered })
        verify(
            mockAppStore,
            times(1)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraPreviewOnTriggered })
    }

    @Test
    fun callingMiddlewareActionHandler_turnCameraOn_fails_then_dispatchCameraOffFailed() {
        // arrange
        val cameraStateCompletableFuture: CompletableFuture<String> = CompletableFuture()
        val appState = AppReduxState("", false, false)
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED,)

        val mockCallingService: CallingService = mock {
            on { turnCameraOn() } doReturn cameraStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doAnswer { appState }
            on { dispatch(any()) } doAnswer { }
        }

        val error = Exception("test")

        // act
        handler.turnCameraOn(mockAppStore)
        cameraStateCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOnFailed &&
                    action.error.cause == error && action.error.errorCode == ErrorCode.TURN_CAMERA_ON_FAILED
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_turnMicOff_while_mic_State_processing() {
        // arrange
        val audioStateCompletableFuture: CompletableFuture<String> = CompletableFuture()

        val appState = AppReduxState("", false, false)
        appState.localParticipantState =
            LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL
                ),
                AudioState(
                    AudioOperationalStatus.PENDING,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                videoStreamID = null,
                displayName = "username",
                localParticipantRole = null
            )

        val mockCallingService: CallingService = mock {
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
        }

        // act
        handler.turnMicOff(mockAppStore)
        audioStateCompletableFuture.complete("")

        // assert
        verify(mockAppStore, times(0)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOffTriggered
            }
        )
        verify(mockCallingService, times(0)).turnMicOff()
    }

    @Test
    fun callingMiddlewareActionHandler_turnMicOff_fails_then_dispatchChangeMicError() {
        // arrange
        val audioStateCompletableFuture: CompletableFuture<Void> = CompletableFuture()

        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.ON,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )

        val mockCallingService: CallingService = mock {
            on { turnMicOff() } doReturn audioStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        val error = Exception("test")

        // act
        handler.turnMicOff(mockAppStore)
        audioStateCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOffFailed &&
                    action.error.cause == error && action.error.errorCode == ErrorCode.TURN_MIC_OFF_FAILED
            }
        )
        verify(mockCallingService, times(1)).turnMicOff()
    }

    @Test
    fun callingMiddlewareActionHandler_turnMicOn_fails_then_dispatchChangeMicError() {
        // arrange
        val audioStateCompletableFuture: CompletableFuture<Void> = CompletableFuture()

        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = null,
            displayName = "username",
            localParticipantRole = null
        )

        val mockCallingService: CallingService = mock {
            on { turnMicOn() } doReturn audioStateCompletableFuture
        }

        val handler = callingMiddlewareActionHandlerImpl(mockCallingService)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        val error = Exception("test")

        // act
        handler.turnMicOn(mockAppStore)
        audioStateCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOnFailed &&
                    action.error.cause == error && action.error.errorCode == ErrorCode.TURN_MIC_ON_FAILED
            }
        )
        verify(mockCallingService, times(1)).turnMicOn()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_dispatch_CallStateErrorOccurred() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.value = CallInfoModel(
                CallingStatus.DISCONNECTED,
                CallStateError(
                    ErrorCode.TOKEN_EXPIRED
                )
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == ErrorCode.TOKEN_EXPIRED
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_dispatch_CallDecline() =
        runScopedTest {
            // arrange
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = provideAppStore()

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.value = CallInfoModel(
                CallingStatus.DISCONNECTED,
                CallStateError(CALL_END_FAILED, CALL_DECLINED)
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated && action.callingState == CallingStatus.NONE
                }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.DISCONNECTED
                }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action -> action is NavigationAction.SetupLaunched }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.callCompositeEventCode == CALL_DECLINED
                }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_verify_CallDecline_Sequence() =
        runScopedTest {
            // arrange
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val callIdFlow = MutableStateFlow<String?>(null)
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = provideAppStore()
            val appStoreSequence = inOrder(mockAppStore)

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.value = CallInfoModel(
                CallingStatus.DISCONNECTED,
                CallStateError(CALL_END_FAILED, CALL_DECLINED)
            )

            // assert
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated && action.callingState == CallingStatus.NONE
                }
            )
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.DISCONNECTED
                }
            )
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED &&
                        action.callStateError.callCompositeEventCode == CALL_DECLINED
                }
            )
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action -> action is NavigationAction.SetupLaunched }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_dispatch_CallEviction() =
        runScopedTest {
            // arrange
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = provideAppStore()

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.value = CallInfoModel(
                CallingStatus.DISCONNECTED,
                CallStateError(CALL_END_FAILED, CALL_EVICTED)
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated && action.callingState == CallingStatus.NONE
                }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.DISCONNECTED
                }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action -> action is NavigationAction.SetupLaunched }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.callCompositeEventCode == CALL_EVICTED
                }
            )
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_verify_CallEviction_Sequence() =
        runScopedTest {
            // arrange
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = provideAppStore()
            val appStoreSequence = inOrder(mockAppStore)

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.value = CallInfoModel(
                CallingStatus.DISCONNECTED,
                CallStateError(CALL_END_FAILED, CALL_EVICTED)
            )

            // assert
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated && action.callingState == CallingStatus.NONE
                }
            )
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.DISCONNECTED
                }
            )
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED &&
                        action.callStateError.callCompositeEventCode == CALL_EVICTED
                }
            )
            appStoreSequence.verify(mockAppStore).dispatch(
                argThat { action -> action is NavigationAction.SetupLaunched }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdateTypeEndCall_then_dispatch_stateUpdatedAndSetupLaunched() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(
                CallInfoModel(
                    CallingStatus.NONE,
                    CallStateError(CALL_END_FAILED)
                )
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.IsTranscribingUpdated &&
                        !action.isTranscribing
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.IsRecordingUpdated &&
                        !action.isRecording
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED
                }
            )

            verify(mockAppStore, times(3)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.NONE
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.SetupLaunched
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_dispatch_stateUpdatedAndCallLaunched() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callConfig = CallConfiguration(
                groupId = null,
                meetingLink = null,
                meetingId = null,
                meetingPasscode = null,
                roomId = null,
                callType = CallType.ONE_TO_N_OUTGOING
            )
            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                UnconfinedTestContextProvider(),
                configuration,
                CapabilitiesManager(CallType.GROUP_CALL)
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(
                CallInfoModel(
                    CallingStatus.CONNECTING,
                    null
                )
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.CallLaunched
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdateConnected_then_dispatch_stateUpdatedAndCallLaunched() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callConfig = CallConfiguration(
                groupId = null,
                meetingLink = null,
                meetingId = null,
                meetingPasscode = null,
                roomId = null,
                callType = CallType.ONE_TO_N_OUTGOING
            )
            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                UnconfinedTestContextProvider(),
                configuration,
                CapabilitiesManager(CallType.GROUP_CALL)
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(
                CallInfoModel(
                    CallingStatus.CONNECTED,
                    null
                )
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.CallLaunched
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_dispatch_stateUpdatedAndExit() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callConfig = CallConfiguration(
                groupId = null,
                meetingLink = null,
                meetingId = null,
                meetingPasscode = null,
                roomId = null,
                callType = CallType.ONE_TO_N_OUTGOING
            )
            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                UnconfinedTestContextProvider(),
                configuration,
                CapabilitiesManager(CallType.GROUP_CALL)
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(
                CallInfoModel(
                    CallingStatus.DISCONNECTED,
                    null
                )
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdateStateDisconnectWithNoError_then_dispatchNavigationExit() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(CallInfoModel(CallingStatus.DISCONNECTED, null))

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.NONE
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.DISCONNECTED
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_OnSubscribeCallInfoModelUpdateStateDisconnectWithError_then_notDispatchNavigationExit() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(
                CallInfoModel(
                    CallingStatus.DISCONNECTED,
                    CallStateError(
                        ErrorCode.TURN_CAMERA_OFF_FAILED
                    )
                )
            )

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.errorCode == CALL_END_FAILED
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.NONE
                }
            )

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.DISCONNECTED
                }
            )

            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
        }

    @Test
    fun callingMiddlewareActionHandler_onSubscribeCamerasCountChangeUpdate_then_dispatch_camerasCountUpdated() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.localParticipantState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.PAUSED,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                    2,
                    null
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                videoStreamID = null,
                displayName = "username",
                localParticipantRole = null
            )
            appState.callState = CallingState(CallingStatus.LOCAL_HOLD,)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow =
                MutableStateFlow(CallInfoModel(CallingStatus.LOCAL_HOLD, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val cameraStateCompletableFuture = CompletableFuture<String>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            cameraStateCompletableFuture.complete("1345")
            camerasCountUpdatedStateFlow.value = 8

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is LocalParticipantAction.CamerasCountUpdated &&
                        action.count == 8
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_admitAll_then_callServiceAdmitAll_testWithErrorCode() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val resultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { admitAll() } doReturn resultCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }

            val error = CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
            handler.admitAll(mockAppStore)
            resultCompletableFuture.complete(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ParticipantAction.LobbyError &&
                        action.code == error
                }
            )
            verify(mockCallingService, times(1)).admitAll()
        }

    @Test
    fun callingMiddlewareActionHandler_admitAll_then_callServiceAdmitAll_testWithNoErrorCode() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val resultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { admitAll() } doReturn resultCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
            }

            handler.admitAll(mockAppStore)
            resultCompletableFuture.complete(null)

            // assert
            verify(mockAppStore, times(0)).dispatch(any())
            verify(mockCallingService, times(1)).admitAll()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_admit_then_callServiceAdmit_testWithErrorCode() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val resultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { admit(any()) } doReturn resultCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }

            val error = CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
            handler.admit("id", mockAppStore)
            resultCompletableFuture.complete(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ParticipantAction.LobbyError &&
                        action.code == error
                }
            )
            verify(mockCallingService, times(1)).admit(
                argThat { action ->
                    action == "id"
                }
            )
        }

    @Test
    fun callingMiddlewareActionHandler_admit_then_callServiceAdmit_testWithNoErrorCode() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val resultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { admit(any()) } doReturn resultCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
            }

            handler.admit("id", mockAppStore)
            resultCompletableFuture.complete(null)

            // assert
            verify(mockAppStore, times(0)).dispatch(any())
            verify(mockCallingService, times(1)).admit(
                argThat { action ->
                    action == "id"
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_decline_then_callServiceDecline_testWithErrorCode() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val resultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { reject(any()) } doReturn resultCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
            }

            val error = CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
            handler.reject("id", mockAppStore)
            resultCompletableFuture.complete(error)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ParticipantAction.LobbyError &&
                        action.code == error
                }
            )
            verify(mockCallingService, times(1)).reject(
                argThat { action ->
                    action == "id"
                }
            )
        }

    @Test
    fun callingMiddlewareActionHandler_decline_then_callServiceDecline_testWithNoErrorCode() =
        runScopedTest {
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.NONE,)

            val resultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()
            val mockCallingService: CallingService = mock {
                on { reject(any()) } doReturn resultCompletableFuture
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
            }

            handler.reject("id", mockAppStore)
            resultCompletableFuture.complete(null)

            // assert
            verify(mockAppStore, times(0)).dispatch(any())
            verify(mockCallingService, times(1)).reject(
                argThat { action ->
                    action == "id"
                }
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_onAudioDeviceChangeRequested_toBluetooth_then_callServiceChangeAudioDevice() =
        runScopedTest {
            val routeToService = CallAudioState.ROUTE_BLUETOOTH
            val selection = AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED
            audioDeviceChangedTests(routeToService, selection)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_onAudioDeviceChangeRequested_toSpeaker_then_callServiceChangeAudioDevice() =
        runScopedTest {
            val routeToService = CallAudioState.ROUTE_SPEAKER
            val selection = AudioDeviceSelectionStatus.SPEAKER_REQUESTED
            audioDeviceChangedTests(routeToService, selection)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_onAudioDeviceChangeRequested_toEarpiece_then_callServiceChangeAudioDevice() =
        runScopedTest {
            val routeToService = CallAudioState.ROUTE_EARPIECE
            val selection = AudioDeviceSelectionStatus.RECEIVER_REQUESTED
            audioDeviceChangedTests(routeToService, selection)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_onAudioDeviceChangeRequested_toEarpiece_then_doNotCallAudioForTelecom_ifSDKOptionNotSet() = runScopedTest {
        val routeToService = CallAudioState.ROUTE_EARPIECE
        val selection = AudioDeviceSelectionStatus.RECEIVER_REQUESTED

        val appState = AppReduxState("", false, false)
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock { }

        val configuration = CallCompositeConfiguration()
        configuration.telecomManagerOptions = CallCompositeTelecomManagerOptions(
            CallCompositeTelecomManagerIntegrationMode.APPLICATION_IMPLEMENTED_TELECOM_MANAGER,
        )

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            configuration,
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        handler.onAudioDeviceChangeRequested(selection, mockAppStore)

        // assert
        verify(mockCallingService, times(0)).setTelecomManagerAudioRoute(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_onAudioDeviceChangeRequested_doNotCallService_if_telecomOptionNotSet() = runScopedTest {
        val appState = AppReduxState("", false, false)
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock {}

        val configuration = CallCompositeConfiguration()
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            configuration,
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        handler.onAudioDeviceChangeRequested(AudioDeviceSelectionStatus.RECEIVER_REQUESTED, mockAppStore)

        // assert
        verify(mockCallingService, times(0)).setTelecomManagerAudioRoute(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_onAudioFocusRequesting_then_dispatchAudioFocusApproved() = runScopedTest {
        val appState = AppReduxState("", false, false)
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock {}

        val configuration = CallCompositeConfiguration()
        configuration.telecomManagerOptions = CallCompositeTelecomManagerOptions(
            CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
            "com.example.telecom.TelecomManager",
        )

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            configuration,
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        handler.onAudioFocusRequesting(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is AudioSessionAction.AudioFocusApproved
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_onAudioFocusRequesting_then_dispatchNoAction_ifTelecomActionNotSet() = runScopedTest {
        val appState = AppReduxState("", false, false)
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock {}

        val configuration = CallCompositeConfiguration()
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            configuration,
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        val mockAppStore = mock<AppStore<ReduxState>> { }

        handler.onAudioFocusRequesting(mockAppStore)

        // assert
        verify(mockAppStore, times(0)).dispatch(any())
    }

    private fun audioDeviceChangedTests(
        routeToService: Int,
        selection: AudioDeviceSelectionStatus
    ) {
        val appState = AppReduxState("", false, false)
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock {
            on { setTelecomManagerAudioRoute(routeToService) } doAnswer { }
        }

        val configuration = CallCompositeConfiguration()
        configuration.telecomManagerOptions = CallCompositeTelecomManagerOptions(
            CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
            "com.example.telecom.TelecomManager"
        )

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            configuration,
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        handler.onAudioDeviceChangeRequested(selection, mockAppStore)

        // assert
        verify(mockCallingService, times(1)).setTelecomManagerAudioRoute(
            argThat { arg ->
                arg == routeToService
            }
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_subscribeOnLocalParticipantRoleChanged_then_notifyRoleChanged() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val localParticipantRoleSharedFlow = MutableSharedFlow<ParticipantRole?>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsReceivedSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsEnabledChangedSharedFlow = MutableSharedFlow<Boolean>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn localParticipantRoleSharedFlow
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            localParticipantRoleSharedFlow.emit(ParticipantRole.PRESENTER)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is LocalParticipantAction.RoleChanged &&
                        action.participantRole == ParticipantRole.PRESENTER
                }
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_startCaptions_success_then_dispatchCaptionsStarted() = runScopedTest {
        // arrange
        val mockCallingService: CallingService = mock {
            on { startCaptions(any()) } doReturn CompletableFuture.completedFuture(null)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.startCaptions("en-US", mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is CaptionsAction.UpdateStatus && action.status == CaptionsStatus.STARTED }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_startCaptions_failure_then_dispatchCaptionsErrorAndStopped() = runScopedTest {
        // arrange
        val exception = CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED)
        val error = Error(exception)
        val mockCallingService: CallingService = mock {
            on { startCaptions(any()) } doReturn CompletableFuture.failedFuture(error)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.startCaptions("en-US", mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ErrorAction.CallStateErrorOccurred && action.callStateError.errorCode == ErrorCode.CAPTIONS_START_FAILED_SPOKEN_LANGUAGE_NOT_SUPPORTED }
        )
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ToastNotificationAction.ShowNotification && action.kind == ToastNotificationKind.CAPTIONS_FAILED_TO_START }
        )
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is CaptionsAction.UpdateStatus && action.status == CaptionsStatus.STOPPED }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_stopCaptions_success_then_dispatchCaptionsStopped() = runScopedTest {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.rttState = RttState(isRttActive = false)

        val mockCallingService: CallingService = mock {
            on { stopCaptions() } doReturn CompletableFuture.completedFuture(null)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
            on { getCurrentState() } doAnswer { appState }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.stopCaptions(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is CaptionsAction.UpdateStatus && action.status == CaptionsStatus.STOPPED }
        )

        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is RttAction.UpdateMaximized && !action.isMaximized }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_stopCaptions_failure_then_dispatchCaptionsError() = runScopedTest {
        // arrange
        val exception = CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE)
        val error = Error(exception)
        val mockCallingService: CallingService = mock {
            on { stopCaptions() } doReturn CompletableFuture.failedFuture(error)
        }

        val appState = AppReduxState("", false, false)
        appState.rttState = RttState(isRttActive = false)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
            on { getCurrentState() } doAnswer { appState }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.stopCaptions(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ToastNotificationAction.ShowNotification && action.kind == ToastNotificationKind.CAPTIONS_FAILED_TO_STOP }
        )
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ErrorAction.CallStateErrorOccurred && action.callStateError.errorCode == ErrorCode.CAPTIONS_NOT_ACTIVE }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_stopCaptions_andRttActive_then_dispatchUpdateMaximizedNotFired() = runScopedTest {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.rttState = RttState(isRttActive = true)

        val exception = CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE)
        val error = Error(exception)
        val mockCallingService: CallingService = mock {
            on { stopCaptions() } doReturn CompletableFuture.failedFuture(error)
        }

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
            on { getCurrentState() } doAnswer { appState }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.stopCaptions(mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ToastNotificationAction.ShowNotification && action.kind == ToastNotificationKind.CAPTIONS_FAILED_TO_STOP }
        )
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ErrorAction.CallStateErrorOccurred && action.callStateError.errorCode == ErrorCode.CAPTIONS_NOT_ACTIVE }
        )
        verify(mockAppStore, times(0)).dispatch(
            argThat { action -> action is RttAction.UpdateMaximized }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_setCaptionsSpokenLanguage_success() = runScopedTest {
        // arrange
        val mockCallingService: CallingService = mock {
            on { setCaptionsSpokenLanguage(any()) } doReturn CompletableFuture.completedFuture(null)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {}

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.setCaptionsSpokenLanguage("fr", mockAppStore)

        // assert
        verify(mockAppStore, never()).dispatch(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_setCaptionsSpokenLanguage_failure_then_dispatchCaptionsError() = runScopedTest {
        // arrange
        val exception = CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE)
        val error = Error(exception)
        val mockCallingService: CallingService = mock {
            on { setCaptionsSpokenLanguage(any()) } doReturn CompletableFuture.failedFuture(error)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.setCaptionsSpokenLanguage("fr", mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ToastNotificationAction.ShowNotification && action.kind == ToastNotificationKind.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE }
        )
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ErrorAction.CallStateErrorOccurred && action.callStateError.errorCode == ErrorCode.CAPTIONS_NOT_ACTIVE }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_setCaptionsCaptionLanguage_success() = runScopedTest {
        // arrange
        val mockCallingService: CallingService = mock {
            on { setCaptionsCaptionLanguage(any()) } doReturn CompletableFuture.completedFuture(null)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {}

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.setCaptionsCaptionLanguage("es", mockAppStore)

        // assert
        verify(mockAppStore, never()).dispatch(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callingMiddlewareActionHandler_setCaptionsCaptionLanguage_failure_then_dispatchCaptionsError() = runScopedTest {
        // arrange
        val exception = CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE)
        val error = Error(exception)
        val mockCallingService: CallingService = mock {
            on { setCaptionsCaptionLanguage(any()) } doReturn CompletableFuture.failedFuture(error)
        }
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

        // act
        handler.setCaptionsCaptionLanguage("es", mockAppStore)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ToastNotificationAction.ShowNotification && action.kind == ToastNotificationKind.CAPTIONS_FAILED_TO_SET_CAPTION_LANGUAGE }
        )
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is ErrorAction.CallStateErrorOccurred && action.callStateError.errorCode == ErrorCode.CAPTIONS_NOT_ACTIVE }
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_dispatchCaptionsStartRequested() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow: MutableSharedFlow<MutableMap<String, ParticipantInfoModel>> =
                MutableSharedFlow()

            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val configuration = CallCompositeConfiguration()
            val localOptions = CallCompositeLocalOptions()
            localOptions.captionsOptions = CallCompositeCaptionsOptions().setCaptionsOn(true)
            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                UnconfinedTestContextProvider(),
                configuration,
                CapabilitiesManager(CallType.GROUP_CALL),
                localOptions
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(CallInfoModel(CallingStatus.CONNECTED, null, callEndReasonSubCode = 123, callEndReasonCode = 456))

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.StartRequested
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_dispatchCaptionsUpdatedActionToStore() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE,
                        0
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val languages = listOf("abc")

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val handler = callingMiddlewareActionHandlerImpl(mockCallingService)

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            captionsSupportedSpokenLanguagesSharedFlow.emit(languages)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.SupportedSpokenLanguagesChanged &&
                        action.languages == languages
                }
            )

            // act
            captionsSupportedCaptionLanguagesSharedFlow.emit(languages)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.SupportedCaptionLanguagesChanged &&
                        action.languages == languages
                }
            )

            // act
            isCaptionsTranslationSupportedSharedFlow.emit(true)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.IsTranslationSupportedChanged &&
                        action.isSupported
                }
            )

            // act
            activeSpokenLanguageChangedSharedFlow.emit("abc")

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.SpokenLanguageChanged &&
                        action.language == "abc"
                }
            )

            // act
            activeCaptionLanguageChangedSharedFlow.emit("abc")

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.CaptionLanguageChanged &&
                        action.language == "abc"
                }
            )

            // act
            captionsTypeChangedSharedFlow.emit(CallCompositeCaptionsType.COMMUNICATION)

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CaptionsAction.TypeChanged &&
                        action.type == CallCompositeCaptionsType.COMMUNICATION
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_then_callTelecomManagerAudioSelection() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTING,)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(
                        CameraOperationalStatus.OFF,
                        CameraDeviceSelectionStatus.FRONT,
                        CameraTransmissionStatus.REMOTE,
                        0
                    ),
                    AudioState(
                        AudioOperationalStatus.OFF,
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                        BluetoothState(available = false, deviceName = "bluetooth")
                    ),
                    "",
                    "",
                    localParticipantRole = null
                )
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val camerasCountUpdatedStateFlow = MutableStateFlow(2)
            val dominantSpeakersSharedFlow = MutableSharedFlow<List<String>>()
            val networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
            val networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
            val mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
            val capabilitiesChangedEventSharedFlow = MutableSharedFlow<CapabilitiesChangedEvent>()
            val totalRemoteParticipantCountSharedFlow = MutableSharedFlow<Int>()
            val captionsSupportedSpokenLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val captionsSupportedCaptionLanguagesSharedFlow = MutableSharedFlow<List<String>>()
            val isCaptionsTranslationSupportedSharedFlow = MutableSharedFlow<Boolean>()
            val activeSpokenLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val activeCaptionLanguageChangedSharedFlow = MutableSharedFlow<String>()
            val captionsTypeChangedSharedFlow = MutableSharedFlow<CallCompositeCaptionsType>()
            /*  <CALL_START_TIME>
            val callStartTimeSharedFlow = MutableSharedFlow<Date>()
            </CALL_START_TIME> */
            val rttFlow = MutableSharedFlow<RttMessage>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallIdStateFlow() } doReturn callIdFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getCamerasCountStateFlow() } doReturn camerasCountUpdatedStateFlow
                on { getDominantSpeakersSharedFlow() } doReturn dominantSpeakersSharedFlow
                on { getLocalParticipantRoleSharedFlow() } doReturn MutableSharedFlow()
                on { getNetworkQualityCallDiagnosticsFlow() } doReturn networkQualityCallDiagnosticsSharedFlow
                on { getNetworkCallDiagnosticsFlow() } doReturn networkCallDiagnosticsSharedFlow
                on { getMediaCallDiagnosticsFlow() } doReturn mediaCallDiagnosticsSharedFlow
                on { getCapabilitiesChangedEventSharedFlow() } doReturn capabilitiesChangedEventSharedFlow
                on { getTotalRemoteParticipantCountSharedFlow() } doReturn totalRemoteParticipantCountSharedFlow
                on { getCaptionsSupportedSpokenLanguagesSharedFlow() } doReturn captionsSupportedSpokenLanguagesSharedFlow
                on { getCaptionsSupportedCaptionLanguagesSharedFlow() } doReturn captionsSupportedCaptionLanguagesSharedFlow
                on { getIsCaptionsTranslationSupportedSharedFlow() } doReturn isCaptionsTranslationSupportedSharedFlow
                on { getActiveSpokenLanguageChangedSharedFlow() } doReturn activeSpokenLanguageChangedSharedFlow
                on { getActiveCaptionLanguageChangedSharedFlow() } doReturn activeCaptionLanguageChangedSharedFlow
                on { getCaptionsTypeChangedSharedFlow() } doReturn captionsTypeChangedSharedFlow
                on { setTelecomManagerAudioRoute(any()) } doAnswer { }
                /*  <CALL_START_TIME>
                on { getCallStartTimeSharedFlow() } doReturn callStartTimeSharedFlow
                </CALL_START_TIME> */
                on { getRttFlow() } doReturn rttFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.telecomManagerOptions = CallCompositeTelecomManagerOptions(
                CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
                "com.example.telecom.TelecomManager",
            )

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                UnconfinedTestContextProvider(),
                configuration,
                CapabilitiesManager(CallType.GROUP_CALL)
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(CallInfoModel(CallingStatus.CONNECTED, null))

            // assert
            verify(mockCallingService, times(1)).setTelecomManagerAudioRoute(
                argThat { arg ->
                    arg == CallAudioState.ROUTE_SPEAKER
                }
            )
        }

    private fun callingMiddlewareActionHandlerImpl(mockCallingService: CallingService) =
        CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            UnconfinedTestContextProvider(),
            CallCompositeConfiguration(),
            CapabilitiesManager(CallType.GROUP_CALL)
        )

    private fun provideAppStore(): AppStore<ReduxState> {
        val appState = AppReduxState(
            "CallingMiddleWareActionHandlerUnitTest",
            false,
            false,
        )
        return mock {
            on { dispatch(any()) } doAnswer { }
            on { getCurrentState() } doAnswer { appState }
        }
    }
}
