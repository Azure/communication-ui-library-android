// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware.handler

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.redux.AppStore
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
internal class CameraSwitchingHandlerTest : ACSBaseTestCoroutine() {

    @Test
    fun cameraSwitchingHandler_switchCamera_success_then_dispatch_CameraSwitchSucceeded() {
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

        // Mock camera switch future
        val cameraSwitchCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()

        val mockCallingService: CallingService = mock {
            on { switchCamera() } doReturn cameraSwitchCompletableFuture
        }

        val handler = CameraSwitchingHandler(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.switchCamera(mockAppStore)
        cameraSwitchCompletableFuture.complete(CameraDeviceSelectionStatus.BACK)

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchSucceeded &&
                    action.cameraDeviceSelectionStatus == CameraDeviceSelectionStatus.BACK
            }
        )
    }

    @Test
    fun cameraSwitchingHandler_switchCamera_fails_with_camera_not_initialized_then_retry_and_succeed() {
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

        // Mock camera switch future that fails with "Camera not initialized" error
        val cameraSwitchCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()
        val error = Exception("Camera not initialized")
        
        // Mock camera off future
        val cameraOffCompletableFuture = CompletableFuture<Void>()
        
        // Mock camera on future
        val cameraOnCompletableFuture = CompletableFuture<String>()
        
        // Mock second camera switch future that succeeds
        val cameraSwitchRetryCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()

        val mockCallingService: CallingService = mock {
            on { switchCamera() } doReturn cameraSwitchCompletableFuture doReturn cameraSwitchRetryCompletableFuture
            on { turnCameraOff() } doReturn cameraOffCompletableFuture
            on { turnCameraOn() } doReturn cameraOnCompletableFuture
        }

        val handler = CameraSwitchingHandler(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act - first attempt fails
        handler.switchCamera(mockAppStore)
        cameraSwitchCompletableFuture.completeExceptionally(error)
        
        // camera off succeeds
        cameraOffCompletableFuture.complete(null)
        
        // camera on succeeds
        cameraOnCompletableFuture.complete("videoStreamId")
        
        // second attempt succeeds
        cameraSwitchRetryCompletableFuture.complete(CameraDeviceSelectionStatus.BACK)

        // assert
        verify(mockCallingService, times(1)).turnCameraOff()
        verify(mockCallingService, times(1)).turnCameraOn()
        verify(mockCallingService, times(2)).switchCamera()
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchSucceeded &&
                    action.cameraDeviceSelectionStatus == CameraDeviceSelectionStatus.BACK
            }
        )
    }

    @Test
    fun cameraSwitchingHandler_switchCamera_fails_with_camera_error_then_retry_and_succeed() {
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

        // Mock camera switch future that fails with "Camera error" error
        val cameraSwitchCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()
        val error = Exception("Camera error")
        
        // Mock camera off future
        val cameraOffCompletableFuture = CompletableFuture<Void>()
        
        // Mock camera on future
        val cameraOnCompletableFuture = CompletableFuture<String>()
        
        // Mock second camera switch future that succeeds
        val cameraSwitchRetryCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()

        val mockCallingService: CallingService = mock {
            on { switchCamera() } doReturn cameraSwitchCompletableFuture doReturn cameraSwitchRetryCompletableFuture
            on { turnCameraOff() } doReturn cameraOffCompletableFuture
            on { turnCameraOn() } doReturn cameraOnCompletableFuture
        }

        val handler = CameraSwitchingHandler(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act - first attempt fails
        handler.switchCamera(mockAppStore)
        cameraSwitchCompletableFuture.completeExceptionally(error)
        
        // camera off succeeds
        cameraOffCompletableFuture.complete(null)
        
        // camera on succeeds
        cameraOnCompletableFuture.complete("videoStreamId")
        
        // second attempt succeeds
        cameraSwitchRetryCompletableFuture.complete(CameraDeviceSelectionStatus.BACK)

        // assert
        verify(mockCallingService, times(1)).turnCameraOff()
        verify(mockCallingService, times(1)).turnCameraOn()
        verify(mockCallingService, times(2)).switchCamera()
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchSucceeded &&
                    action.cameraDeviceSelectionStatus == CameraDeviceSelectionStatus.BACK
            }
        )
    }

    @Test
    fun cameraSwitchingHandler_switchCamera_fails_with_other_error_then_dispatch_CameraSwitchFailed() {
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

        // Mock camera switch future that fails with a different error
        val cameraSwitchCompletableFuture = CompletableFuture<CameraDeviceSelectionStatus>()
        val error = Exception("Some other error")

        val mockCallingService: CallingService = mock {
            on { switchCamera() } doReturn cameraSwitchCompletableFuture
        }

        val handler = CameraSwitchingHandler(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.switchCamera(mockAppStore)
        cameraSwitchCompletableFuture.completeExceptionally(error)

        // assert
        verify(mockCallingService, times(0)).turnCameraOff()
        verify(mockCallingService, times(0)).turnCameraOn()
        verify(mockCallingService, times(1)).switchCamera()
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchFailed &&
                    action.error.cause == error &&
                    action.error.errorCode == ErrorCode.SWITCH_CAMERA_FAILED
            }
        )
    }

    @Test
    fun cameraSwitchingHandler_switchCamera_when_camera_is_not_on_then_dispatch_CameraSwitchFailed() {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.OFF, // Camera is OFF
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

        val mockCallingService: CallingService = mock()

        val handler = CameraSwitchingHandler(mockCallingService)

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getCurrentState() } doReturn appState
            on { dispatch(any()) } doAnswer { }
        }

        // act
        handler.switchCamera(mockAppStore)

        // assert
        verify(mockCallingService, times(0)).switchCamera()
        
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchFailed &&
                    action.error.errorCode == ErrorCode.SWITCH_CAMERA_FAILED &&
                    action.error.cause?.message == "Cannot switch camera when camera is not on"
            }
        )
    }
}
