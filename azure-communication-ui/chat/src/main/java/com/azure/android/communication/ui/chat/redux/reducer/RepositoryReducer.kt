// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.RepositoryAction
import com.azure.android.communication.ui.chat.redux.state.RepositoryState

internal interface RepositoryReducer : Reducer<RepositoryState>

internal class RepositoryReducerImpl : RepositoryReducer {
    override fun reduce(
        state: RepositoryState,
        action: Action,
    ): RepositoryState {
        return when (action) {
            is RepositoryAction.RepositoryUpdated -> state.copy(lastUpdatedTimestamp = System.currentTimeMillis())
            else -> state
        }
    }
}
