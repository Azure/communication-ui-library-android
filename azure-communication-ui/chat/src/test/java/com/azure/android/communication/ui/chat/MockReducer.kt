// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.reducer.Reducer
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal class MockReducer : Reducer<ReduxState> {
    var lastAction: Action? = null
    override fun reduce(state: ReduxState, action: Action): ReduxState {
        lastAction = action
        return state
    }
}
