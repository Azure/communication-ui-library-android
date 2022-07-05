// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup.LeaveConfirmViewModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
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
internal class LeaveConfirmViewModelUnitTest {

    @Test
    fun leaveConfirmViewModel_confirm_then_dispatchEndCall() {

        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore::dispatch)

        leaveConfirmViewModel.confirm()

        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallingAction.CallEndRequested
            }
        )
    }

    @Test
    fun leaveConfirmViewModel_cancel_then_isLeaveConfirmDisplayed_updated() {

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore::dispatch)

        leaveConfirmViewModel.cancel()

        Assert.assertEquals(
            false,
            leaveConfirmViewModel.getShouldDisplayLeaveConfirmFlow().value
        )
    }

    @Test
    fun leaveConfirmViewModel_requestExitConfirmation_then_isLeaveConfirmDisplayed_updated() {

        val mockAppStore = mock<AppStore<ReduxState>> {}

        val leaveConfirmViewModel = LeaveConfirmViewModel(mockAppStore::dispatch)

        leaveConfirmViewModel.requestExitConfirmation()

        Assert.assertEquals(
            true,
            leaveConfirmViewModel.getShouldDisplayLeaveConfirmFlow().value
        )
    }
}
