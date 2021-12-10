package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class SetupParticipantAvatarViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarTrue_when_videoStreamIDHasNoValueAndHasCameraPermissions() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel()
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.DENIED, PermissionStatus.DENIED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
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
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarFalse_when_videoStreamIDHasValueAndHasCameraPermissions() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel()
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                "id",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
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
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarFalse_when_videoStreamIDHasNoValueAndHasCameraPermissionsDenied() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel()
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("", PermissionState(PermissionStatus.GRANTED, PermissionStatus.DENIED))

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
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarFalse_when_videoStreamIDHasNoValueAndHasAudioPermissionsDenied() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel()
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("", PermissionState(PermissionStatus.DENIED, PermissionStatus.GRANTED))

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
