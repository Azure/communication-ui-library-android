// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.presentation.fragment.calling.connecting.overlay.ConnectingOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.InitialCallJoinState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class ConnectingOverlayViewModelTest : ACSBaseTestCoroutine() {
    @Test
    fun connectingLobbyOverlayViewModel_when_callingStateChange_then_notifyLobbyState() =
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val viewModel = ConnectingOverlayViewModel(mockAppStore::dispatch)
            viewModel.init(
                CallingState(
                    callingStatus = CallingStatus.NONE,
                ),
                PermissionState(
                    audioPermissionState = PermissionStatus.GRANTED,
                    cameraPermissionState = PermissionStatus.GRANTED
                ),
                mockNetworkManager,
                CameraState(
                    operation = CameraOperationalStatus.PENDING,
                    device = CameraDeviceSelectionStatus.FRONT,
                    transmission = CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    operation = AudioOperationalStatus.ON,
                    device = AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    bluetoothState = BluetoothState(available = false, deviceName = "")
                ),
                initialCallJoinState = InitialCallJoinState(skipSetupScreen = true)
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getDisplayOverlayFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                CallingState(
                    callingStatus = CallingStatus.CONNECTED,
                ),
                CameraOperationalStatus.ON,
                PermissionState(
                    audioPermissionState = PermissionStatus.GRANTED,
                    cameraPermissionState = PermissionStatus.GRANTED
                ),
                AudioOperationalStatus.ON,
                InitialCallJoinState(skipSetupScreen = true)
            )

            // assert
            Assert.assertEquals(2, modelFlow.count())
            Assert.assertEquals(true, modelFlow[0])
            Assert.assertEquals(false, modelFlow[1])

            displayLobbyJob.cancel()
        }

    @Test
    fun connectingLobbyOverlayViewModel_when_callTypeOutgoing_then_doNotDisplayOverlay() =
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val viewModel = ConnectingOverlayViewModel(mockAppStore::dispatch, false, callType = CallType.ONE_TO_N_OUTGOING)
            viewModel.init(
                CallingState(
                    callingStatus = CallingStatus.CONNECTING,
                ),
                PermissionState(
                    audioPermissionState = PermissionStatus.GRANTED,
                    cameraPermissionState = PermissionStatus.GRANTED
                ),
                mockNetworkManager,
                CameraState(
                    operation = CameraOperationalStatus.PENDING,
                    device = CameraDeviceSelectionStatus.FRONT,
                    transmission = CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    operation = AudioOperationalStatus.ON,
                    device = AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    bluetoothState = BluetoothState(available = false, deviceName = "")
                ),
                initialCallJoinState = InitialCallJoinState(skipSetupScreen = true)
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getDisplayOverlayFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                CallingState(
                    callingStatus = CallingStatus.CONNECTED,
                ),
                CameraOperationalStatus.ON,
                PermissionState(
                    audioPermissionState = PermissionStatus.GRANTED,
                    cameraPermissionState = PermissionStatus.GRANTED
                ),
                AudioOperationalStatus.ON,
                InitialCallJoinState(skipSetupScreen = true)
            )

            // assert
            Assert.assertEquals(1, modelFlow.count())
            Assert.assertEquals(false, modelFlow[0])

            displayLobbyJob.cancel()
        }
}
