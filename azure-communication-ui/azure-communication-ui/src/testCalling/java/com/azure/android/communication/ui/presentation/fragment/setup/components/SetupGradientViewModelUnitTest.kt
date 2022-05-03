package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupGradientViewModel
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
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
internal class SetupGradientViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun setupGradientViewModel_onUpdate_then_notifyGradientEnabled_when_videoStreamIDHasValue() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupGradientViewModel()
            viewModel.init("", CameraOperationalStatus.ON)

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getDisplaySetupGradientFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("id1", CameraOperationalStatus.ON)

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
    fun setupGradientViewModel_onUpdate_then_notifyGradientDisabled_when_videoStreamIDHasNoValue() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupGradientViewModel()
            viewModel.init("id1", CameraOperationalStatus.ON)

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getDisplaySetupGradientFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("", CameraOperationalStatus.ON)

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
    fun setupGradientViewModel_onUpdate_then_notifyGradientDisabled_when_videoStreamHasValueAndCameraIsNotOn() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupGradientViewModel()
            viewModel.init("id1", CameraOperationalStatus.ON)

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getDisplaySetupGradientFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("id3", CameraOperationalStatus.OFF)

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
