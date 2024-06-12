// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
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
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
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
        val appState = AppReduxState("", false, false, false)
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

        val callingViewModel = ControlBarViewModel(mockAppStore::dispatch, CapabilitiesManager(CallType.GROUP_CALL))
        callingViewModel.turnMicOn()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOnTriggered
            }
        )
    }

    @Test
    fun controlBarViewModel_turnMicOn_then_dispatchTurnMicOff() {
        val appState = AppReduxState("", false, false, false)
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

        val callingViewModel = ControlBarViewModel(mockAppStore::dispatch, CapabilitiesManager(CallType.GROUP_CALL))
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
            val visibilityState = VisibilityState(status = VisibilityStatus.VISIBLE)
            val avMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
            val capabilities = setOf(ParticipantCapabilityType.UNMUTE_MICROPHONE)

            val appStore = mock<AppStore<ReduxState>> { }
            val callingViewModel = ControlBarViewModel(appStore::dispatch, CapabilitiesManager(CallType.GROUP_CALL))
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
                ),
                {},
                {},
                {},
                visibilityState,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilities = capabilities
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
                callingViewModel.audioOperationalStatus
                    .toList(resultListFromAudioStateFlow)
            }

            // act
            callingViewModel.update(
                permissionState,
                cameraState,
                audioState1,
                CallingStatus.CONNECTED,
                visibilityState,
                avMode,
                capabilities,
            )
            callingViewModel.update(
                permissionState,
                cameraState,
                audioState2,
                CallingStatus.CONNECTED,
                visibilityState,
                avMode,
                capabilities,
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
            val permissionState1 = PermissionState(
                audioPermissionState = PermissionStatus.DENIED,
                cameraPermissionState = PermissionStatus.DENIED
            )

            val permissionState2 = PermissionState(
                audioPermissionState = PermissionStatus.DENIED,
                cameraPermissionState = PermissionStatus.GRANTED
            )

            val cameraState = CameraState(
                operation = CameraOperationalStatus.OFF,
                device = CameraDeviceSelectionStatus.FRONT,
                transmission = CameraTransmissionStatus.REMOTE
            )
            val audioDeviceState = AudioDeviceSelectionStatus.RECEIVER_SELECTED

            val resultListFromIsCameraButtonEnabledStateFlow = mutableListOf<Boolean>()
            val visibilityState = VisibilityState(status = VisibilityStatus.VISIBLE)
            val avMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
            val capabilities = emptySet<ParticipantCapabilityType>()

            val appStore = mock<AppStore<ReduxState>>()
            val callingViewModel = ControlBarViewModel(appStore::dispatch, CapabilitiesManager(CallType.GROUP_CALL))
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
                ),
                {},
                {},
                {},
                visibilityState,
                avMode,
                capabilities,
            )

            val flowJob = launch {
                callingViewModel.isCameraButtonEnabled
                    .toList(resultListFromIsCameraButtonEnabledStateFlow)
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
                visibilityState,
                avMode,
                capabilities,
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
                visibilityState,
                avMode,
                capabilities,
            )

            // assert
            Assert.assertEquals(
                false,
                resultListFromIsCameraButtonEnabledStateFlow[1]
            )

            Assert.assertEquals(
                true,
                resultListFromIsCameraButtonEnabledStateFlow[2]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun controlBarViewModel_update_then_cameraStateFlowReflectsUpdate() {
        runScopedTest {
            // arrange
            val appStore = mock<AppStore<ReduxState>>()
            val callingViewModel = ControlBarViewModel(appStore::dispatch, CapabilitiesManager(CallType.GROUP_CALL))

            val permissionState = PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.GRANTED
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
            val visibilityState = VisibilityState(status = VisibilityStatus.VISIBLE)
            val avMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
            val capabilities = emptySet<ParticipantCapabilityType>()

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
                ),
                {},
                {},
                {},
                visibilityState,
                avMode,
                capabilities,
            )

            val resultListFromCameraStateFlow = mutableListOf<CameraOperationalStatus>()
            val flowJob = launch {
                callingViewModel.cameraStatus.toList(resultListFromCameraStateFlow)
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
                visibilityState,
                avMode,
                capabilities,
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
                visibilityState,
                avMode,
                capabilities,
            )

            // assert
            Assert.assertEquals(
                expectedCameraState1,
                resultListFromCameraStateFlow[1]
            )

            Assert.assertEquals(
                expectedCameraState2,
                resultListFromCameraStateFlow[2]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun controlBarViewModel_update_then_callingStatusFlowReflectsUpdate() {
        runScopedTest {
            // arrange
            val appStore = mock<AppStore<ReduxState>>()
            val callingViewModel = ControlBarViewModel(appStore::dispatch, CapabilitiesManager(CallType.GROUP_CALL))

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
            val visibilityState = VisibilityState(status = VisibilityStatus.VISIBLE)
            val avMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
            val capabilities = setOf(ParticipantCapabilityType.TURN_VIDEO_ON, ParticipantCapabilityType.UNMUTE_MICROPHONE)

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
                ),
                {},
                {},
                {},
                visibilityState,
                avMode,
                capabilities,
            )

            val resultListFromIsCameraButtonEnabledStateFlow = mutableListOf<Boolean>()
            val resultListFromIsMicButtonEnabledStateFlow = mutableListOf<Boolean>()
            val resultListFromIsAudioDeviceButtonEnabledStateFlow = mutableListOf<Boolean>()
            val flowJob1 = launch {
                callingViewModel.isCameraButtonEnabled.toList(resultListFromIsCameraButtonEnabledStateFlow)
            }
            val flowJob2 = launch {
                callingViewModel.isMicButtonEnabled.toList(resultListFromIsMicButtonEnabledStateFlow)
            }
            val flowJob3 = launch {
                callingViewModel.isAudioDeviceButtonEnabled.toList(resultListFromIsAudioDeviceButtonEnabledStateFlow)
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
                visibilityState,
                avMode,
                capabilities,
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
                visibilityState,
                avMode,
                capabilities,
            )

            // assert
            Assert.assertEquals(
                true,
                resultListFromIsCameraButtonEnabledStateFlow[0]
            )

            Assert.assertEquals(
                false,
                resultListFromIsCameraButtonEnabledStateFlow[1]
            )
            Assert.assertEquals(
                true,
                resultListFromIsMicButtonEnabledStateFlow[0]
            )

            Assert.assertEquals(
                false,
                resultListFromIsMicButtonEnabledStateFlow[1]
            )
            Assert.assertEquals(
                true,
                resultListFromIsAudioDeviceButtonEnabledStateFlow[0]
            )

            Assert.assertEquals(
                false,
                resultListFromIsAudioDeviceButtonEnabledStateFlow[1]
            )

            flowJob1.cancel()
            flowJob2.cancel()
            flowJob3.cancel()
        }
    }
}
