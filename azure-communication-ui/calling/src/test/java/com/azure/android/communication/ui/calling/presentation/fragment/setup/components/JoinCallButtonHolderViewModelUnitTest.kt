// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.media.AudioManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class JoinCallButtonHolderViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun joinCallButtonHolderViewModel_onUpdate_then_notifyButtonEnabled_when_audioPermissionStateIsGranted() =
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val mockAudioManager = mock<AudioManager>()

            val viewModel = JoinCallButtonHolderViewModel(mockAppStore::dispatch, mockAudioManager)
            viewModel.init(
                PermissionStatus.DENIED,
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2,
                mockNetworkManager
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getJoinCallButtonEnabledFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                PermissionStatus.GRANTED,
                CallingState(CallingStatus.NONE, OperationStatus.NONE),
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2
            )

            // assert
            Assert.assertEquals(
                false,
                emitResult[0]
            )

            Assert.assertEquals(
                true,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun joinCallButtonHolderViewModel_onUpdate_then_notifyButtonDisabled_when_audioPermissionStateIsNotGranted() =
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val mockAudioManager = mock<AudioManager>()

            val viewModel = JoinCallButtonHolderViewModel(mockAppStore::dispatch, mockAudioManager)
            viewModel.init(
                PermissionStatus.GRANTED,
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2,
                networkManager = mockNetworkManager
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getJoinCallButtonEnabledFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                PermissionStatus.DENIED,
                CallingState(CallingStatus.NONE, OperationStatus.NONE),
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2
            )

            // assert
            Assert.assertEquals(
                true,
                emitResult[0]
            )

            Assert.assertEquals(
                false,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun joinCallButtonHolderViewModel_launchCallScreen_then_notifyButtonDisabled() =
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val mockAudioManager = mock<AudioManager>()

            val viewModel = JoinCallButtonHolderViewModel(mockAppStore::dispatch, mockAudioManager)
            viewModel.init(
                PermissionStatus.GRANTED,
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2,
                mockNetworkManager
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getDisableJoinCallButtonFlow().toList(emitResult)
            }

            // act
            viewModel.launchCallScreen()

            // assert
            Assert.assertEquals(false, emitResult[0])

            // act
            viewModel.update(
                PermissionStatus.GRANTED,
                CallingState(CallingStatus.CONNECTING, OperationStatus.NONE),
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2
            )

            // assert
            // Should have disabled during connecting
            Assert.assertEquals(2, emitResult.count())
            Assert.assertEquals(true, emitResult[1])

            // act
            viewModel.update(
                PermissionStatus.GRANTED,
                CallingState(CallingStatus.NONE, OperationStatus.NONE),
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2
            )

            // assert
            // no more emits yet
            Assert.assertEquals(false, emitResult[2])

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun joinCallButtonHolderViewModel_onUpdate_then_notifyButtonDisabled_when_cameraPermissionStateIsUnknown() =
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val mockAudioManager = mock<AudioManager>()

            val viewModel = JoinCallButtonHolderViewModel(mockAppStore::dispatch, mockAudioManager)
            viewModel.init(
                PermissionStatus.GRANTED,
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2,
                mockNetworkManager
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getJoinCallButtonEnabledFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                PermissionStatus.GRANTED,
                CallingState(CallingStatus.NONE, OperationStatus.NONE),
                PermissionStatus.UNKNOWN,
                CameraOperationalStatus.ON,
                2
            )

            // assert
            Assert.assertEquals(
                true,
                emitResult[0]
            )

            Assert.assertEquals(
                false,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun joinCallButtonHolderViewModel_onUpdate_then_notifyButtonDisabled_when_cameraOperationStateIsPending() =
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val mockNetworkManager = mock<NetworkManager>()
            val mockAudioManager = mock<AudioManager>()

            val viewModel = JoinCallButtonHolderViewModel(mockAppStore::dispatch, mockAudioManager)
            viewModel.init(
                PermissionStatus.GRANTED,
                PermissionStatus.GRANTED,
                CameraOperationalStatus.ON,
                2,
                mockNetworkManager
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getJoinCallButtonEnabledFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                PermissionStatus.GRANTED,
                CallingState(CallingStatus.NONE, OperationStatus.NONE),
                PermissionStatus.UNKNOWN,
                CameraOperationalStatus.PENDING,
                2
            )

            // assert
            Assert.assertEquals(
                true,
                emitResult[0]
            )

            Assert.assertEquals(
                false,
                emitResult[1]
            )

            resultFlow.cancel()
        }
}
