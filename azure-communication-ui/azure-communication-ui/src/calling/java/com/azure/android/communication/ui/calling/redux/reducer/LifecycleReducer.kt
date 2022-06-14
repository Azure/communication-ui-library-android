// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.state.LifecycleState
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import org.reduxkotlin.Reducer


internal class LifecycleReducer : Reducer<LifecycleState> {
    override fun invoke(state: LifecycleState, action: Any): LifecycleState {
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
