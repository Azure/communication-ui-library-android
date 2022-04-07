// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.presentation.fragment.setup.components.PermissionWarningViewModel
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
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
internal class PermissionWarningViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun permissionWarningViewModel_onUpdate_then_notifyPermissionsViewModelStateFlow() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val initialExpectedAudioPermissionState = PermissionStatus.UNKNOWN
            val initialExpectedCameraPermissionState = PermissionStatus.UNKNOWN
            val updatedExpectedAudioPermissionState = PermissionStatus.GRANTED
            val updatedExpectedCameraPermissionState = PermissionStatus.GRANTED
            val expectedPermissionState = PermissionState(
                updatedExpectedAudioPermissionState,
                updatedExpectedCameraPermissionState
            )
            val mockAppStore = mock<AppStore<ReduxState>>()
            val permissionsViewModel = PermissionWarningViewModel(mockAppStore::dispatch)

            val initialPermissionState = PermissionState(
                PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN
            )
            permissionsViewModel.init(initialPermissionState)

            val emitResultFromCameraFlow = mutableListOf<PermissionStatus>()
            val emitResultFromAudioFlow = mutableListOf<PermissionStatus>()

            val audioFlowJob = launch {
                permissionsViewModel.getAudioPermissionStateFlow()
                    .toList(emitResultFromAudioFlow)
            }

            val cameraFlowJob = launch {
                permissionsViewModel.getCameraPermissionStateFlow()
                    .toList(emitResultFromCameraFlow)
            }

            // act
            permissionsViewModel.update(expectedPermissionState)

            // assert
            Assert.assertEquals(
                initialExpectedAudioPermissionState,
                emitResultFromAudioFlow[0]
            )

            Assert.assertEquals(
                initialExpectedCameraPermissionState,
                emitResultFromCameraFlow[0]
            )

            Assert.assertEquals(
                updatedExpectedAudioPermissionState,
                emitResultFromAudioFlow[1]
            )

            Assert.assertEquals(
                updatedExpectedCameraPermissionState,
                emitResultFromCameraFlow[1]
            )

            audioFlowJob.cancel()
            cameraFlowJob.cancel()
        }

    @Test
    fun permissionWarningViewModel_turnCameraOn_then_dispatchCameraOnTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val permissionWarningViewModel = PermissionWarningViewModel(mockAppStore::dispatch)

        // Act
        permissionWarningViewModel.turnCameraOn()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPreviewOnRequested
            }
        )
    }
}
