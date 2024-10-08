// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CaptionsLayoutViewModelUnitTest : ACSBaseTestCoroutine() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLinearLayoutViewModelUnitTest_update_then_notShowCaptions() {
        runScopedTest {

            // arrange
            val viewModel = CaptionsViewModel()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            viewModel.init(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            val displayErrorJob = launch {
                viewModel.getDisplayCaptionsInfoViewFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            // act
            viewModel.update(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            // assert
            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultDisplayErrorHeaderStateFlow.count()
            )

            displayErrorJob.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLinearLayoutViewModelUnitTest_update_then_notShowCaptionsForVisibility() {
        runScopedTest {

            // arrange
            val viewModel = CaptionsViewModel()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            viewModel.init(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            val displayErrorJob = launch {
                viewModel.getDisplayCaptionsInfoViewFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            // act
            viewModel.update(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.STARTED),
                VisibilityState(VisibilityStatus.PIP_MODE_ENTERED)
            )

            // assert
            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultDisplayErrorHeaderStateFlow.count()
            )

            displayErrorJob.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLinearLayoutViewModelUnitTest_update_then_showCaptions() {
        runScopedTest {

            // arrange
            val viewModel = CaptionsViewModel()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            viewModel.init(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            val displayErrorJob = launch {
                viewModel.getDisplayCaptionsInfoViewFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            // act
            viewModel.update(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.START_REQUESTED),
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            // assert
            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                true,
                resultDisplayErrorHeaderStateFlow[1]
            )

            Assert.assertEquals(
                2,
                resultDisplayErrorHeaderStateFlow.count()
            )

            displayErrorJob.cancel()
        }
    }
}
