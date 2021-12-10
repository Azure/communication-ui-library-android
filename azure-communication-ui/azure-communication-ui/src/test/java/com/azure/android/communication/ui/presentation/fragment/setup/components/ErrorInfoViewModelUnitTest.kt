// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ErrorState
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
internal class ErrorInfoViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun snackBarViewModel_onUpdate_then_notifyCallStateErrorFlow() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val expectedPermissionState = CallStateError(CallCompositeErrorCode.CALL_END)
            val appState = AppReduxState("")
            appState.errorState = ErrorState(null, expectedPermissionState)

            val snackBarViewModel = ErrorInfoViewModel()

            val emitResult = mutableListOf<CallStateError?>()

            val resultFlow = launch {
                snackBarViewModel.getCallStateErrorStateFlow()
                    .toList(emitResult)
            }

            // act
            snackBarViewModel.update(appState.errorState)

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
