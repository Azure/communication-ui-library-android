// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LifecycleAction
import com.azure.android.communication.ui.redux.state.LifecycleState
import com.azure.android.communication.ui.redux.state.LifecycleStatus

internal interface LifecycleReducer : Reducer<LifecycleState>

internal class LifecycleReducerImpl : LifecycleReducer {
    override fun reduce(state: LifecycleState, action: Action): LifecycleState {
        return when (action) {
            is LifecycleAction.EnterBackgroundSucceeded -> {
                LifecycleState(LifecycleStatus.BACKGROUND)
            }
            is LifecycleAction.EnterForegroundSucceeded -> {
                LifecycleState(LifecycleStatus.FOREGROUND)
            }
            else -> state
        }
    }
}
