// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.controlbar

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
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
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureState
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class ControlBarViewModelUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun controlBarViewModel_turnMicOn_then_dispatchTurnMicOn() {
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
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

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val callingViewModel = ControlBarViewModel(mockAppStore::dispatch)
        callingViewModel.turnMicOn()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOnTriggered
            }
        )
    }

    @Test
    fun controlBarViewModel_turnMicOn_then_dispatchTurnMicOff() {
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = LocalUserState(
            CameraState(
                CameraOperationalStatus.PAUSED,
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

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val callingViewModel = ControlBarViewModel(mockAppStore::dispatch)
        callingViewModel.turnMicOff()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOffTriggered
            }
        )
    }

    @Test
    fun controlBarViewModel_update_then_audioStateFlowReflectsUpdate() {
        runScopedTest {

            val permissionState = PermissionState(PermissionStatus.DENIED, PermissionStatus.DENIED)
            val cameraState = CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            )
            val audioDeviceState = AudioDeviceSelectionStatus.RECEIVER_SELECTED
            val pipState = PictureInPictureState(status = PictureInPictureStatus.VISIBLE)

            val appStore = mock<AppStore<ReduxState>> { }
            val callingViewModel = ControlBarViewModel(appStore::dispatch)
            callingViewModel.init(
                permissionState,
                cameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingState(
                    CallingStatus.CONNECTED,
                    OperationStatus.NONE
                ),
                {},
                {},
                {},
                pipState,
            )

            val expectedAudioOperationalStatus1 = AudioOperationalStatus.ON
            val expectedAudioOperationalStatus2 = AudioOperationalStatus.OFF

            val audioState1 = AudioState(
                expectedAudioOperationalStatus1, audioDeviceState,
                BluetoothState(available = false, deviceName = "bluetooth")
            )
            val audioState2 = AudioState(
                expectedAudioOperationalStatus2, audioDeviceState,
                BluetoothState(available = false, deviceName = "bluetooth")
            )

            val resultListFromAudioStateFlow = mutableListOf<AudioOperationalStatus>()
            val flowJob = launch {
                callingViewModel.getAudioOperationalStatusStateFlow()
                    .toList(resultListFromAudioStateFlow)
            }

            // act
            callingViewModel.update(
                permissionState,
                cameraState,
                audioState1,
                CallingStatus.CONNECTED,
                pipState,
            )
            callingViewModel.update(
                permissionState,
                cameraState,
                audioState2,
                CallingStatus.CONNECTED,
                pipState,
            )

            // assert

            Assert.assertEquals(expectedAudioOperationalStatus1, resultListFromAudioStateFlow[1])
            Assert.assertEquals(expectedAudioOperationalStatus2, resultListFromAudioStateFlow[2])

            flowJob.cancel()
        }
    }

    @Test
    fun controlBarViewModel_update_then_cameraPermissionStateFlowReflectsUpdate() {
        runScopedTest {

            val expectedCameraPermissionState1 = PermissionStatus.DENIED
            val expectedCameraPermissionState2 = PermissionStatus.GRANTED

            val permissionState1 = PermissionState(
                PermissionStatus.DENIED,
                expectedCameraPermissionState1
            )

            val permissionState2 = PermissionState(
                PermissionStatus.DENIED,
                expectedCameraPermissionState2
            )

            val cameraState = CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.REMOTE
            )
            val audioDeviceState = AudioDeviceSelectionStatus.RECEIVER_SELECTED

            val resultListFromCameraPermissionStateFlow =
                mutableListOf<ControlBarViewModel.CameraModel>()
            val pipState = PictureInPictureState(status = PictureInPictureStatus.VISIBLE)

            val appStore = mock<AppStore<ReduxState>>()
            val callingViewModel = ControlBarViewModel(appStore::dispatch)
            val initialPermissionState = PermissionState(
                PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN
            )

            callingViewModel.init(
                initialPermissionState,
                cameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingState(
                    CallingStatus.CONNECTED,
                    OperationStatus.NONE
                ),
                {},
                {},
                {},
                pipState,
            )

            val flowJob = launch {
                callingViewModel.getCameraStateFlow()
                    .toList(resultListFromCameraPermissionStateFlow)
            }

            // act
            callingViewModel.update(
                permissionState1,
                cameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingStatus.CONNECTED,
                pipState,
            )
            callingViewModel.update(
                permissionState2,
                cameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingStatus.CONNECTED,
                pipState,
            )

            // assert
            Assert.assertEquals(
                expectedCameraPermissionState1,
                resultListFromCameraPermissionStateFlow[1].cameraPermissionState
            )

            Assert.assertEquals(
                expectedCameraPermissionState2,
                resultListFromCameraPermissionStateFlow[2].cameraPermissionState
            )

            flowJob.cancel()
        }
    }

    @Test
    fun controlBarViewModel_update_then_cameraStateFlowReflectsUpdate() {
        runScopedTest {
            // arrange
            val appStore = mock<AppStore<ReduxState>>()
            val callingViewModel = ControlBarViewModel(appStore::dispatch)

            val permissionState = PermissionState(
                PermissionStatus.GRANTED,
                PermissionStatus.GRANTED
            )

            val audioDeviceState = AudioDeviceSelectionStatus.RECEIVER_SELECTED
            val cameraDeviceSelectionStatus = CameraDeviceSelectionStatus.FRONT
            val cameraTransmissionStatus = CameraTransmissionStatus.REMOTE

            val expectedCameraState1 = CameraOperationalStatus.ON
            val expectedCameraState2 = CameraOperationalStatus.OFF

            val cameraState1 = CameraState(
                expectedCameraState1,
                cameraDeviceSelectionStatus,
                cameraTransmissionStatus
            )

            val cameraState2 = CameraState(
                expectedCameraState2,
                cameraDeviceSelectionStatus,
                cameraTransmissionStatus
            )

            val initialCameraState = CameraState(
                CameraOperationalStatus.OFF,
                cameraDeviceSelectionStatus,
                cameraTransmissionStatus
            )
            val pipState = PictureInPictureState(status = PictureInPictureStatus.VISIBLE)

            callingViewModel.init(
                permissionState,
                initialCameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingState(
                    CallingStatus.CONNECTED,
                    OperationStatus.NONE
                ),
                {},
                {},
                {},
                pipState,
            )

            val resultListFromCameraStateFlow = mutableListOf<ControlBarViewModel.CameraModel>()
            val flowJob = launch {
                callingViewModel.getCameraStateFlow().toList(resultListFromCameraStateFlow)
            }

            // act
            callingViewModel.update(
                permissionState,
                cameraState1,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingStatus.CONNECTED,
                pipState,
            )
            callingViewModel.update(
                permissionState,
                cameraState2,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingStatus.CONNECTED,
                pipState,
            )

            // assert
            Assert.assertEquals(
                expectedCameraState1,
                resultListFromCameraStateFlow[1].cameraState.operation
            )

            Assert.assertEquals(
                expectedCameraState2,
                resultListFromCameraStateFlow[2].cameraState.operation
            )

            flowJob.cancel()
        }
    }

    @Test
    fun controlBarViewModel_update_then_callingStatusFlowReflectsUpdate() {
        runScopedTest {
            // arrange
            val appStore = mock<AppStore<ReduxState>>()
            val callingViewModel = ControlBarViewModel(appStore::dispatch)

            val permissionState = PermissionState(
                PermissionStatus.GRANTED,
                PermissionStatus.GRANTED
            )

            val audioDeviceState = AudioDeviceSelectionStatus.RECEIVER_SELECTED
            val cameraDeviceSelectionStatus = CameraDeviceSelectionStatus.FRONT
            val cameraTransmissionStatus = CameraTransmissionStatus.REMOTE

            val expectedCameraState = CameraOperationalStatus.ON

            val cameraState = CameraState(
                expectedCameraState,
                cameraDeviceSelectionStatus,
                cameraTransmissionStatus
            )

            val initialCameraState = CameraState(
                CameraOperationalStatus.OFF,
                cameraDeviceSelectionStatus,
                cameraTransmissionStatus
            )
            val pipState = PictureInPictureState(status = PictureInPictureStatus.VISIBLE)

            callingViewModel.init(
                permissionState,
                initialCameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingState(
                    CallingStatus.CONNECTED,
                    OperationStatus.NONE
                ),
                {},
                {},
                {},
                pipState,
            )

            val resultListFromOnHoldCallStatusStateFlow = mutableListOf<Boolean>()
            val flowJob = launch {
                callingViewModel.getOnHoldCallStatusStateFlowStateFlow().toList(resultListFromOnHoldCallStatusStateFlow)
            }

            // act
            callingViewModel.update(
                permissionState,
                cameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingStatus.CONNECTED,
                pipState,
            )
            callingViewModel.update(
                permissionState,
                cameraState,
                AudioState(
                    AudioOperationalStatus.OFF,
                    audioDeviceState,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                CallingStatus.LOCAL_HOLD,
                pipState,
            )

            // assert
            Assert.assertEquals(
                false,
                resultListFromOnHoldCallStatusStateFlow[0]
            )

            Assert.assertEquals(
                true,
                resultListFromOnHoldCallStatusStateFlow[1]
            )

            flowJob.cancel()
        }
    }
}
