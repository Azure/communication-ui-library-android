// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.handler

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.helper.UnconfinedTestContextProvider
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
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
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
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
import java.util.concurrent.CompletableFuture

@RunWith(MockitoJUnitRunner::class)
internal class CameraSwitchingAfterBackgroundTest : ACSBaseTestCoroutine() {

    @Test
    fun callingMiddlewareActionHandler_switchCamera_after_background_foreground_transition_then_dispatch_CameraSwitchSucceeded() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
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
            videoStreamID = "videoStreamId",
            displayName = "username",
            localParticipantRole = null
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.CONNECTED)

        // Mock camera off future for enterBackground
        val cameraOffCompletableFuture = CompletableFuture<Void>()
        
        // Mock camera on future for enterForeground
        val cameraOnCompletableFuture = CompletableFuture<String>()
        
        // Mock camera switch future
        val cameraSwitchCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraOffCompletableFuture
            on { turnCameraOn() } doReturn cameraOnCompletableFuture
            on { switchCamera() } doReturn cameraSwitchCompletableFuture
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

        // act - simulate background transition
        handler.enterBackground(mockAppStore)
        cameraOffCompletableFuture.complete(null)

        // Update app state to reflect camera paused
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "videoStreamId",
            displayName = "username",
            localParticipantRole = null
        )

        // act - simulate foreground transition
        handler.enterForeground(mockAppStore)
        cameraOnCompletableFuture.complete("videoStreamId")

        // Update app state to reflect camera on
        appState.localParticipantState = LocalUserState(
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
            videoStreamID = "videoStreamId",
            displayName = "username",
            localParticipantRole = null
        )

        // act - try to switch camera
        handler.switchCamera(mockAppStore)
        cameraSwitchCompletableFuture.complete(CameraDeviceSelectionStatus.BACK)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LifecycleAction.EnterBackgroundSucceeded
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPauseSucceeded
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LifecycleAction.EnterForegroundSucceeded
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOnSucceeded &&
                    action.videoStreamID == "videoStreamId"
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchSucceeded &&
                        action.cameraDeviceSelectionStatus == CameraDeviceSelectionStatus.BACK
            }
        )
    }

    @Test
    fun callingMiddlewareActionHandler_switchCamera_after_background_foreground_transition_then_dispatch_CameraSwitchFailed() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
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
            videoStreamID = "videoStreamId",
            displayName = "username",
            localParticipantRole = null
        )
        appState.navigationState = NavigationState(NavigationStatus.IN_CALL)
        appState.callState = CallingState(CallingStatus.CONNECTED)

        // Mock camera off future for enterBackground
        val cameraOffCompletableFuture = CompletableFuture<Void>()
        
        // Mock camera on future for enterForeground
        val cameraOnCompletableFuture = CompletableFuture<String>()
        
        // Mock camera switch future
        val cameraSwitchCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()
        val error = Exception("Camera switch failed")

        val mockCallingService: CallingService = mock {
            on { turnCameraOff() } doReturn cameraOffCompletableFuture
            on { turnCameraOn() } doReturn cameraOnCompletableFuture
            on { switchCamera() } doReturn cameraSwitchCompletableFuture
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

        // act - simulate background transition
        handler.enterBackground(mockAppStore)
        cameraOffCompletableFuture.complete(null)

        // Update app state to reflect camera paused
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            ),
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            videoStreamID = "videoStreamId",
            displayName = "username",
            localParticipantRole = null
        )

        // act - simulate foreground transition
        handler.enterForeground(mockAppStore)
        cameraOnCompletableFuture.complete("videoStreamId")

        // Update app state to reflect camera on
        appState.localParticipantState = LocalUserState(
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
            videoStreamID = "videoStreamId",
            displayName = "username",
            localParticipantRole = null
        )

        // act - try to switch camera
        handler.switchCamera(mockAppStore)
        cameraSwitchCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LifecycleAction.EnterBackgroundSucceeded
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPauseSucceeded
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LifecycleAction.EnterForegroundSucceeded
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOnSucceeded &&
                    action.videoStreamID == "videoStreamId"
            }
        )
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchFailed &&
                    action.error.cause == error &&
                    action.error.errorCode == ErrorCode.SWITCH_CAMERA_FAILED
            }
        )
    }
}
