// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import org.reduxkotlin.Reducer

internal class ErrorReducer : Reducer<ErrorState> {
    override fun invoke(state: ErrorState, action: Any): ErrorState {
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
