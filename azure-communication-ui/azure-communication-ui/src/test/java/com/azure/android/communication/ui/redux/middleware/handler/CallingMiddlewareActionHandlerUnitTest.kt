// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.middleware.handler

import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.helper.TestContextProvider
import com.azure.android.communication.ui.model.CallInfoModel
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.ErrorAction
import com.azure.android.communication.ui.redux.action.LifecycleAction
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.NavigationAction
import com.azure.android.communication.ui.redux.action.ParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CallingState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.LocalUserState
import com.azure.android.communication.ui.redux.state.NavigationState
import com.azure.android.communication.ui.redux.state.NavigationStatus
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.CallingService
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CallingMiddlewareActionHandlerUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun callingMiddlewareActionHandler_turnCameraOff_when_navigationState_inCall_then_dispatchUpdateCameraStateToStore() {
        // arrange
        val cameraStateCompletableFuture = CompletableFuture<Void>()
        val appState = AppReduxState("")
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService, TestContextProvider()
        )
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doAnswer { appState }
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
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.PAUSED, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService, TestContextProvider()
        )
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doAnswer { appState }
            on { dispatch(any()) } doAnswer { }
        }

        val error = Exception("test")

        // act
        handler.turnCameraOff(mockAppStore)
        cameraStateCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOffFailed && action.error.cause == error && action.error.callCompositeErrorCode == CallCompositeErrorCode.TURN_CAMERA_OFF
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_turnCameraOn_when_navigationState_inCall_then_dispatchRequestCameraOnToStore() {
        // arrange
        val appState = AppReduxState("")
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOn() } doReturn cameraStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService, TestContextProvider()
        )
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
        val appState = AppReduxState("")
        appState.permissionState = PermissionState(
            cameraPermissionState = PermissionStatus.NOT_ASKED,
            audioPermissionState = PermissionStatus.NOT_ASKED
        )

        val mockCallingService: CallingService = mock {}

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService, TestContextProvider()
        )
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
        val appState = AppReduxState("")
        appState.permissionState = PermissionState(
            cameraPermissionState = PermissionStatus.GRANTED,
            audioPermissionState = PermissionStatus.NOT_ASKED
        )

        val mockCallingService: CallingService = mock {}

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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
        val appState = AppReduxState("")
        appState.permissionState =
            PermissionState(PermissionStatus.GRANTED, PermissionStatus.NOT_ASKED)
        appState.navigationState = NavigationState(
            NavigationStatus.SETUP
        )
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock {}

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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
        val appState = AppReduxState("")
        appState.permissionState =
            PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.GRANTED)
        appState.navigationState = NavigationState(
            NavigationStatus.SETUP
        )
        appState.callState = CallingState(CallingStatus.NONE)

        val mockCallingService: CallingService = mock {}

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE),
                    AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
                    "",
                    ""
                )
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
            participantMap["user"] =
                ParticipantInfoModel(
                    "user", "id",
                    isMuted = false,
                    isSpeaking = false,
                    screenShareVideoStreamModel = null,
                    cameraVideoStreamModel = null,
                    modifiedTimestamp = 0,
                    speakingTimestamp = 0
                )

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
    fun callingMiddlewareActionHandler_startCall_then_dispatchCallStateUpdateActionToStore() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE),
                    AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
                    "",
                    ""
                )
            val callingServiceParticipantsSharedFlow: MutableSharedFlow<MutableMap<String, ParticipantInfoModel>> =
                MutableSharedFlow()

            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(CallInfoModel(CallingStatus.CONNECTED, null))

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.StateUpdated &&
                        action.callingState == CallingStatus.CONNECTED
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_fails_then_dispatchFatalError() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE),
                    AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
                    "",
                    ""
                )

            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
                        action.error.fatalError == exception && action.error.codeCallComposite == CallCompositeErrorCode.CALL_JOIN
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_startCall_cameraOff_micOff_then_useCorrectCameraAndAudioStates() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val expectedCameraState = CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE)
            val expectedAudioState = AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED)
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    ""
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
    fun callingMiddlewareActionHandler_startCall_cameraOff_micOn_then_useCorrectCameraAndAudioStates() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val expectedCameraState = CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE)
            val expectedAudioState = AudioState(AudioOperationalStatus.ON, AudioDeviceSelectionStatus.SPEAKER_SELECTED)
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    ""
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val expectedCameraState = CameraState(CameraOperationalStatus.ON, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE)
            val expectedAudioState = AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED)
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    ""
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val expectedCameraState = CameraState(CameraOperationalStatus.ON, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE)
            val expectedAudioState = AudioState(AudioOperationalStatus.ON, AudioDeviceSelectionStatus.SPEAKER_SELECTED)
            val appState = AppReduxState("")
            appState.callState = CallingState(CallingStatus.CONNECTED)
            appState.localParticipantState =
                LocalUserState(
                    expectedCameraState,
                    expectedAudioState,
                    "",
                    ""
                )
            val startCallCompletableFuture = CompletableFuture<Void>()
            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn startCallCompletableFuture
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
        val appState = AppReduxState("")
        appState.localParticipantState =
            LocalUserState(
                CameraState(CameraOperationalStatus.ON, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.REMOTE),
                AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
                videoStreamID = null,
                displayName = "username"
            )
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<Void>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.ON, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.callState = CallingState(CallingStatus.NONE)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock {}
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
            times(1)
        ).dispatch(argThat { action -> action is LocalParticipantAction.CameraPauseSucceeded })

        verify(
            mockCallingService,
            times(0)
        ).turnCameraOff()
    }

    @Test
    fun callingMiddlewareActionHandler_enterBackground_then_doNothingAlreadyInBackground() {
        // arrange
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.PAUSED, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.PAUSED, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOn() } doReturn cameraStateCompletableFuture
        }
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
    fun callingMiddlewareActionHandler_enterForeground_then_dispatch_DoNotTurnCameraOnIfWasNotPaused() {
        // arrange
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.PAUSED, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )
        appState.callState = CallingState(CallingStatus.NONE)

        val cameraStateCompletableFuture = CompletableFuture<String>()

        val mockCallingService: CallingService = mock()
        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
        val appState = AppReduxState("")
        appState.navigationState = NavigationState(
            NavigationStatus.IN_CALL
        )
        appState.callState = CallingState(CallingStatus.CONNECTED)

        val mockCallingService: CallingService = mock {
            on { turnCameraOn() } doReturn cameraStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )

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
                    action.error.cause == error && action.error.callCompositeErrorCode == CallCompositeErrorCode.TURN_CAMERA_ON
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_turnMicOff_while_mic_State_processing() {
        // arrange
        val audioStateCompletableFuture: CompletableFuture<String> = CompletableFuture()

        val appState = AppReduxState("")
        appState.localParticipantState =
            LocalUserState(
                CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
                AudioState(AudioOperationalStatus.PENDING, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
                videoStreamID = null,
                displayName = "username"
            )

        val mockCallingService: CallingService = mock {
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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

        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.ON, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )

        val mockCallingService: CallingService = mock {
            on { turnMicOff() } doReturn audioStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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
                    action.error.cause == error && action.error.callCompositeErrorCode == CallCompositeErrorCode.TURN_MIC_OFF
            }
        )
        verify(mockCallingService, times(1)).turnMicOff()
    }

    @Test
    fun callingMiddlewareActionHandler_turnMicOn_fails_then_dispatchChangeMicError() {
        // arrange
        val audioStateCompletableFuture: CompletableFuture<Void> = CompletableFuture()

        val appState = AppReduxState("")
        appState.localParticipantState = LocalUserState(
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            videoStreamID = null,
            displayName = "username"
        )

        val mockCallingService: CallingService = mock {
            on { turnMicOn() } doReturn audioStateCompletableFuture
        }

        val handler = CallingMiddlewareActionHandlerImpl(
            mockCallingService,
            TestContextProvider()
        )
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
                    action.error.cause == error && action.error.callCompositeErrorCode == CallCompositeErrorCode.TURN_MIC_ON
            }
        )
        verify(mockCallingService, times(1)).turnMicOn()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdate_then_dispatch_CallStateErrorOccurred() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.value = CallInfoModel(
                CallingStatus.DISCONNECTED,
                CallStateError(
                    CallCompositeErrorCode.TOKEN_EXPIRED
                )
            )

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.callCompositeErrorCode == CallCompositeErrorCode.TOKEN_EXPIRED
                }
            )
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdateTypeEndCall_then_dispatch_stateUpdatedAndSetupLaunched() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

            val mockAppStore = mock<AppStore<ReduxState>> {
                on { dispatch(any()) } doAnswer { }
                on { getCurrentState() } doAnswer { appState }
            }

            // act
            handler.startCall(mockAppStore)
            callInfoModelStateFlow.emit(
                CallInfoModel(
                    CallingStatus.NONE,
                    CallStateError(
                        CallCompositeErrorCode.CALL_END
                    )
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
                        action.callStateError.callCompositeErrorCode == CallCompositeErrorCode.CALL_END
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
    fun callingMiddlewareActionHandler_onSubscribeCallInfoModelUpdateStateDisconnectWithNoError_then_dispatchNavigationExit() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
            )

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
                        action.callStateError.callCompositeErrorCode == CallCompositeErrorCode.CALL_END
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
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")

            val callingServiceParticipantsSharedFlow =
                MutableSharedFlow<MutableMap<String, ParticipantInfoModel>>()
            val callInfoModelStateFlow = MutableStateFlow(CallInfoModel(CallingStatus.NONE, null))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            val mockCallingService: CallingService = mock {
                on { getParticipantsInfoModelSharedFlow() } doReturn callingServiceParticipantsSharedFlow
                on { startCall(any(), any()) } doReturn CompletableFuture<Void>()
                on { getCallInfoModelEventSharedFlow() } doReturn callInfoModelStateFlow
                on { getIsMutedSharedFlow() } doReturn isMutedSharedFlow
                on { getIsRecordingSharedFlow() } doReturn isRecordingSharedFlow
                on { getIsTranscribingSharedFlow() } doReturn isTranscribingSharedFlow
            }

            val handler = CallingMiddlewareActionHandlerImpl(
                mockCallingService,
                TestContextProvider()
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
                    CallStateError(
                        CallCompositeErrorCode.TURN_CAMERA_OFF
                    )
                )
            )

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is ErrorAction.CallStateErrorOccurred &&
                        action.callStateError.callCompositeErrorCode == CallCompositeErrorCode.CALL_END
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
}
