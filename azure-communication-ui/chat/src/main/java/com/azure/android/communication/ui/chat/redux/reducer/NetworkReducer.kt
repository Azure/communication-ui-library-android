// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.NetworkAction
import com.azure.android.communication.ui.chat.redux.state.NetworkState
import com.azure.android.communication.ui.chat.redux.state.NetworkStatus

internal interface NetworkReducer : Reducer<NetworkState>

internal class NetworkReducerImpl : NetworkReducer {
    override fun reduce(
        state: NetworkState,
        action: Action,
    ): NetworkState {
        return when (action) {
            is NetworkAction.Connected -> {
                state.copy(networkStatus = NetworkStatus.CONNECTED)
            }
            is NetworkAction.Disconnected -> {
                state.copy(networkStatus = NetworkStatus.DISCONNECTED)
            }
            is NetworkAction.SetDisconnectedOffset -> {
                state.copy(disconnectOffsetDateTime = action.disconnectOffsetDateTime)
            }
            else -> state
        }
    }
}
