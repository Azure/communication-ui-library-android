// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoViewModel
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ErrorInfoViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun snackBarViewModel_onUpdate_then_notifyCallStateErrorFlow() =
        runScopedTest {
            // arrange
            val expectedPermissionState = CallStateError(ErrorCode.CALL_END_FAILED, null)
            val appState = ReduxState("", false, false)
            appState.errorState = ErrorState(null, expectedPermissionState)

            val snackBarViewModel = ErrorInfoViewModel()

            val emitResult = mutableListOf<CallStateError?>()

            val resultFlow = launch {
                snackBarViewModel.getCallStateErrorStateFlow()
                    .toList(emitResult)
            }

            // act
            snackBarViewModel.updateCallStateError(appState.errorState)

            // assert
            Assert.assertEquals(
                null,
                emitResult[0]
            )

            Assert.assertEquals(
                expectedPermissionState,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun errorInfoViewModel_update_when_errorStateNetworkNotAvailable_then_snackBarErrorConnectionErrorDisplayed() =
        runScopedTest {
            // arrange
            val expectedPermissionState = CallStateError(ErrorCode.NETWORK_NOT_AVAILABLE, null)
            val appState = ReduxState("", false, false)

            appState.errorState = ErrorState(null, expectedPermissionState)

            val snackBarViewModel = ErrorInfoViewModel()

            val emitResult = mutableListOf<CallStateError?>()

            val resultFlow = launch {
                snackBarViewModel.getCallStateErrorStateFlow()
                    .toList(emitResult)
            }

            // act
            snackBarViewModel.updateCallStateError(appState.errorState)

            // assert
            Assert.assertEquals(
                null,
                emitResult[0]
            )

            Assert.assertEquals(
                expectedPermissionState,
                emitResult[1]
            )

            resultFlow.cancel()
        }
}
