// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.reducer.CallStateReducerImpl
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallingReducerUnitTest {
    @Test
    fun callingReducer_reduce_when_actionUpdateCallState_then_changeCallingState() {
        // arrange
        val reducer = CallStateReducerImpl()
        val previousState = CallingState(CallingStatus.NONE, OperationStatus.NONE)
        val action = CallingAction.StateUpdated(CallingStatus.CONNECTED)

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(CallingStatus.CONNECTED, newState.callingStatus)
    }

    @Test
    fun callingReducer_reduce_when_actionStartCall_then_doNotChangeCallingState() {
        // arrange
        val reducer = CallStateReducerImpl()
        val previousState = CallingState(CallingStatus.NONE, OperationStatus.NONE)
        val action = CallingAction.CallStartRequested()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(CallingStatus.NONE, newState.callingStatus)
        Assert.assertNotNull(newState.callStartDateTime)
    }
}
