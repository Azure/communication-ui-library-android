package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class SetupParticipantAvatarViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarTrue_when_videoStreamIDHasNoValueAndHasCameraPermissions() =
        runScopedTest {
            // arrange
            val viewModel = getSetupParticipantAvatarViewModel()
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
        runScopedTest {
            // arrange
            val viewModel = getSetupParticipantAvatarViewModel()
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
        runScopedTest {
            // arrange
            val viewModel = getSetupParticipantAvatarViewModel()
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
        runScopedTest {
            // arrange
            val viewModel = getSetupParticipantAvatarViewModel()
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

    private fun getSetupParticipantAvatarViewModel() = SetupParticipantAvatarViewModel()
}
