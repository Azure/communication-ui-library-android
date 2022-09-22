// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.ChatState

internal interface ChatReducer : Reducer<ChatState>

internal class ChatReducerImpl : ChatReducer {
    override fun reduce(state: ChatState, action: Action): ChatState {
        return state
    }
}
