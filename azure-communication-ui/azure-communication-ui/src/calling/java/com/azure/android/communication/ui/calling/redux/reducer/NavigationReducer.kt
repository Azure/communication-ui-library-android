// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import org.reduxkotlin.Reducer


internal class NavigationReducer : Reducer<NavigationState> {
    override fun invoke(state: NavigationState, action: Any): NavigationState {
        return when (action) {
            is NavigationAction.Exit -> {
                NavigationState(NavigationStatus.EXIT)
            }
            is NavigationAction.CallLaunched -> {
                NavigationState(NavigationStatus.IN_CALL)
            }
            is NavigationAction.SetupLaunched -> {
                NavigationState(NavigationStatus.SETUP)
            }
            else -> state
        }
    }
}
