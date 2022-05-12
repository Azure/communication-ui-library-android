// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.models.CommunicationUIErrorCode
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoViewModel
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
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
            val expectedPermissionState = CallStateError(CommunicationUIErrorCode.CALL_END_FAILED, null)
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
