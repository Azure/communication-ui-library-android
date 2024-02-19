// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.LifecycleState

internal interface LifecycleReducer : Reducer<LifecycleState>

internal class LifecycleReducerImpl : LifecycleReducer {
    override fun reduce(
        state: LifecycleState,
        action: Action,
    ): LifecycleState {
        return state
    }
}
