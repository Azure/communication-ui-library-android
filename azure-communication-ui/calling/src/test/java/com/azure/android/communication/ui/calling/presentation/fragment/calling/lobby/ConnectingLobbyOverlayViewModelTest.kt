// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class ConnectingLobbyOverlayViewModelTest : ACSBaseTestCoroutine() {
    @Test
    fun connectingLobbyOverlayViewModel_when_callingStateChange_then_notifyLobbyState() =
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val viewModel = ConnectingLobbyOverlayViewModel(mockAppStore::dispatch)
            viewModel.init(
                CallingState(
                    callingStatus = CallingStatus.NONE,
                    operationStatus = OperationStatus.SKIP_SETUP_SCREEN
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
                )
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getDisplayLobbyOverlayFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                CallingState(
                    callingStatus = CallingStatus.CONNECTED,
                    operationStatus = OperationStatus.SKIP_SETUP_SCREEN
                ),
                CameraOperationalStatus.ON,
                PermissionState(
                    audioPermissionState = PermissionStatus.GRANTED,
                    cameraPermissionState = PermissionStatus.GRANTED
                ),
                AudioOperationalStatus.ON
            )

            // assert
            Assert.assertEquals(2, modelFlow.count())
            Assert.assertEquals(true, modelFlow[0])
            Assert.assertEquals(false, modelFlow[1])

            displayLobbyJob.cancel()
        }
}
