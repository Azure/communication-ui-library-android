// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.NavigationAction
import com.azure.android.communication.ui.chat.redux.state.NavigationState
import com.azure.android.communication.ui.chat.redux.state.NavigationStatus

internal interface NavigationReducer : Reducer<NavigationState>

internal class NavigationReducerImpl : NavigationReducer {
    override fun reduce(
        state: NavigationState,
        action: Action,
    ): NavigationState {
        return when (action) {
            is NavigationAction.GotoParticipants -> {
                state.copy(navigationStatus = NavigationStatus.PARTICIPANTS)
            }
            is NavigationAction.Pop -> {
                state.copy(navigationStatus = NavigationStatus.NONE)
            }
            else -> state
        }
    }
}
