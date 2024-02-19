// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.ErrorState

internal interface ErrorReducer : Reducer<ErrorState>

internal class ErrorReducerImpl : ErrorReducer {
    override fun reduce(
        state: ErrorState,
        action: Action,
    ): ErrorState {
        return when (action) {
            is ErrorAction.FatalErrorOccurred -> {
                state.copy(fatalError = action.error, callStateError = state.callStateError)
            }
            is ErrorAction.CallStateErrorOccurred -> {
                state.copy(fatalError = state.fatalError, callStateError = action.callStateError)
            }
            else -> state
        }
    }
}
