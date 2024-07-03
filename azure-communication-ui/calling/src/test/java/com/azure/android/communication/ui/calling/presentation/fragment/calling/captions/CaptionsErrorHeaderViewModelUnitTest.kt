// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsErrors
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsError
import com.azure.android.communication.ui.calling.redux.state.CaptionsErrorType
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CaptionsErrorHeaderViewModelUnitTest : ACSBaseTestCoroutine() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsErrorHeaderViewModelUnitTest_update_then_showErrorIfStateIsConnected() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val captionsErrorHeaderViewModel = CaptionsErrorHeaderViewModel(mockAppStore::dispatch)
            val resultCaptionsErrorListCellStateFlow =
                mutableListOf<CaptionsError?>()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            captionsErrorHeaderViewModel.init(
                CallingStatus.CONNECTED,
                null,
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            val errorJob = launch {
                captionsErrorHeaderViewModel.getCaptionsErrorFlow()
                    .toList(resultCaptionsErrorListCellStateFlow)
            }

            val displayErrorJob = launch {
                captionsErrorHeaderViewModel.getDisplayCaptionsErrorHeaderFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            val errorFirst = CaptionsError(
                CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_START,
                CaptionsErrorType.CAPTIONS_START_ERROR
            )

            val errorSecond = CaptionsError(
                CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_STOP,
                CaptionsErrorType.CAPTIONS_STOP_ERROR
            )

            // act
            captionsErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                errorFirst,
                VisibilityState(
                    VisibilityStatus.VISIBLE
                )
            )
            captionsErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                errorSecond,
                VisibilityState(
                    VisibilityStatus.VISIBLE
                )
            )

            // assert
            Assert.assertEquals(
                null,
                resultCaptionsErrorListCellStateFlow[0]
            )

            Assert.assertEquals(
                errorFirst,
                resultCaptionsErrorListCellStateFlow[1]
            )

            Assert.assertEquals(
                errorSecond,
                resultCaptionsErrorListCellStateFlow[2]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                true,
                resultDisplayErrorHeaderStateFlow[1]
            )

            errorJob.cancel()
            displayErrorJob.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsErrorHeaderViewModelUnitTest_update_then_notShowErrorIfStateIsNotConnected() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val captionsErrorHeaderViewModel = CaptionsErrorHeaderViewModel(mockAppStore::dispatch)
            val resultCaptionsErrorListCellStateFlow =
                mutableListOf<CaptionsError?>()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            captionsErrorHeaderViewModel.init(
                CallingStatus.CONNECTED,
                null,
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            val errorJob = launch {
                captionsErrorHeaderViewModel.getCaptionsErrorFlow()
                    .toList(resultCaptionsErrorListCellStateFlow)
            }

            val displayErrorJob = launch {
                captionsErrorHeaderViewModel.getDisplayCaptionsErrorHeaderFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            val errorFirst = CaptionsError(
                CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_START,
                CaptionsErrorType.CAPTIONS_START_ERROR
            )

            val errorSecond = CaptionsError(
                CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_STOP,
                CaptionsErrorType.CAPTIONS_STOP_ERROR
            )

            // act
            captionsErrorHeaderViewModel.update(
                CallingStatus.CONNECTING,
                errorFirst,
                VisibilityState(
                    VisibilityStatus.VISIBLE
                )
            )

            // assert
            Assert.assertEquals(
                null,
                resultCaptionsErrorListCellStateFlow[0]
            )

            Assert.assertEquals(
                errorFirst,
                resultCaptionsErrorListCellStateFlow[1]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultDisplayErrorHeaderStateFlow.count()
            )

            errorJob.cancel()
            displayErrorJob.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsErrorHeaderViewModelUnitTest_update_then_notShowErrorIfVisibilityForUIisNotVisible() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val captionsErrorHeaderViewModel = CaptionsErrorHeaderViewModel(mockAppStore::dispatch)
            val resultCaptionsErrorListCellStateFlow =
                mutableListOf<CaptionsError?>()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            captionsErrorHeaderViewModel.init(
                CallingStatus.CONNECTED,
                null,
                VisibilityState(VisibilityStatus.VISIBLE)
            )

            val errorJob = launch {
                captionsErrorHeaderViewModel.getCaptionsErrorFlow()
                    .toList(resultCaptionsErrorListCellStateFlow)
            }

            val displayErrorJob = launch {
                captionsErrorHeaderViewModel.getDisplayCaptionsErrorHeaderFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            val errorFirst = CaptionsError(
                CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_START,
                CaptionsErrorType.CAPTIONS_START_ERROR
            )

            val errorSecond = CaptionsError(
                CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_STOP,
                CaptionsErrorType.CAPTIONS_STOP_ERROR
            )

            // act
            captionsErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                errorFirst,
                VisibilityState(
                    VisibilityStatus.PIP_MODE_ENTERED
                )
            )

            // assert
            Assert.assertEquals(
                null,
                resultCaptionsErrorListCellStateFlow[0]
            )

            Assert.assertEquals(
                errorFirst,
                resultCaptionsErrorListCellStateFlow[1]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultDisplayErrorHeaderStateFlow.count()
            )

            errorJob.cancel()
            displayErrorJob.cancel()
        }
    }

    @Test
    fun captionsErrorHeaderViewModelUnitTest_close_then_dispatchCaptionsAction() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>> {}
        Mockito.`when`(mockAppStore.dispatch(CaptionsAction.ClearError())).then { }
        val captionsErrorHeaderViewModel = CaptionsErrorHeaderViewModel(mockAppStore::dispatch)

        // act
        captionsErrorHeaderViewModel.close()

        // assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CaptionsAction.ClearError
            }
        )
    }
}
