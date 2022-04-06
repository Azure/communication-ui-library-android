// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.state.ReduxState
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
internal class ConfirmLeaveOverlayViewModelUnitTest {

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

        val mockAppStore = mock<AppStore<ReduxState>> {}

        var confirmLeaveOverlayViewModel = ConfirmLeaveOverlayViewModel(mockAppStore::dispatch)

        confirmLeaveOverlayViewModel.cancel()

        Assert.assertEquals(
            false,
            confirmLeaveOverlayViewModel.getShouldDisplayConfirmLeaveOverlayFlow().value
        )
    }
}
