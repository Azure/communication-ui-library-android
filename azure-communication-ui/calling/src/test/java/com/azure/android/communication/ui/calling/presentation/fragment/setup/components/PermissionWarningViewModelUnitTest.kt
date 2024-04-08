// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PermissionWarningViewModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
internal class PermissionWarningViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun permissionWarningViewModel_onUpdate_then_notifyPermissionsViewModelStateFlow() =
        runScopedTest {
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
                permissionsViewModel.audioPermissionStateFlow
                    .toList(emitResultFromAudioFlow)
            }

            val cameraFlowJob = launch {
                permissionsViewModel.cameraPermissionStateFlow
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
