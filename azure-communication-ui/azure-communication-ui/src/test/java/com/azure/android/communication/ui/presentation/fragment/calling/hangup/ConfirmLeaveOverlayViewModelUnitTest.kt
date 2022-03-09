// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.state.ReduxState
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
internal class ConfirmLeaveOverlayViewModelUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun confirmLeaveOverlayViewModel_confirm_then_dispatchEndCall() {

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        var confirmLeaveOverlayViewModel = ConfirmLeaveOverlayViewModel(mockAppStore::dispatch)

        confirmLeaveOverlayViewModel.confirm()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallingAction.CallEndRequested
            }
        )
    }

    @Test
    fun confirmLeaveOverlayViewModel_cancel_then_isConfirmLeaveOverlayDisplayed_updated() {
        mainCoroutineRule.testDispatcher.runBlockingTest {
            val mockAppStore = mock<AppStore<ReduxState>> {}

            var confirmLeaveOverlayViewModel = ConfirmLeaveOverlayViewModel(mockAppStore::dispatch)
            confirmLeaveOverlayViewModel.init(false)
            val resultListFromConfirmLeaveOverlayDisplayStateFlow = mutableListOf<Boolean>()
            val flowJob = launch {
                confirmLeaveOverlayViewModel.getIsConfirmLeaveOverlayDisplayedStateFlow()
                    .toList(resultListFromConfirmLeaveOverlayDisplayStateFlow)
            }

            // act
            confirmLeaveOverlayViewModel.updateConfirmLeaveOverlayDisplayState(true)
            confirmLeaveOverlayViewModel.updateConfirmLeaveOverlayDisplayState(false)

            // assert
            Assert.assertEquals(
                false,
                resultListFromConfirmLeaveOverlayDisplayStateFlow[0]
            )
            Assert.assertEquals(
                true,
                resultListFromConfirmLeaveOverlayDisplayStateFlow[1]
            )
            Assert.assertEquals(
                false,
                resultListFromConfirmLeaveOverlayDisplayStateFlow[2]
            )
            flowJob.cancel()
        }
    }
}
