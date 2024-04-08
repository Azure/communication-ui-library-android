// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.state.LifecycleState
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus

internal interface LifecycleReducer : Reducer<LifecycleState>

internal class LifecycleReducerImpl : LifecycleReducer {
    override fun reduce(state: LifecycleState, action: Action): LifecycleState {
        return when (action) {
            is LifecycleAction.EnterBackgroundSucceeded -> {
                state.copy(state = LifecycleStatus.BACKGROUND)
            }
            is LifecycleAction.EnterForegroundSucceeded -> {
                state.copy(state = LifecycleStatus.FOREGROUND)
            }
            else -> state
        }
    }
}
