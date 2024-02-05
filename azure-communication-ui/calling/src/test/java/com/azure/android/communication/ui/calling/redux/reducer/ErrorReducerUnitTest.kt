// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.calling.error.FatalError
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ErrorReducerUnitTest {
    @Test
    fun errorReducer_reduce_when_actionFatalError_then_changeStateToFatalError() {

        // arrange
        val reducer = ErrorReducerImpl()
        val oldState = ErrorState(null, null)
        val fatalError = FatalError(Exception(), ErrorCode.CALL_JOIN_FAILED)
        val action = ErrorAction.FatalErrorOccurred(fatalError)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(fatalError, newState.fatalError)
    }
}
