// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <RTT_POC> */
package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.RttAction
import com.azure.android.communication.ui.calling.redux.state.RttState

internal interface RttReducer : Reducer<RttState>

internal class RttReducerImpl : RttReducer {
    override fun reduce(state: RttState, action: Action): RttState {

        return when (action) {
            is RttAction.SendRtt -> {
                // Do nothing? I think middleware should handle this
                return state
            }
            is RttAction.IncomingMessageReceived -> {
                state.copy(messages = listOf(action.message), isRttActive = true)
            }
            is RttAction.DisableRttLocally -> {
                state.copy(isRttActive = false)
            }
            else -> state
        }
    }
}
/* </RTT_POC> */
