// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ChatState
import com.azure.android.communication.ui.chat.redux.state.ChatStatus

internal interface ChatReducer : Reducer<ChatState>

internal class ChatReducerImpl : ChatReducer {
    override fun reduce(state: ChatState, action: Action): ChatState {
        return when (action) {
            is ChatAction.ChatThreadID -> {
                state.copy(chatThreadId = action.chatThreadId)
            }
            is ChatAction.Initialization -> {
                state.copy(chatStatus = ChatStatus.INITIALIZATION)
            }
            is ChatAction.Initialized -> {
                state.copy(chatStatus = ChatStatus.INITIALIZED)
            }
            is ChatAction.LocalParticipantInfo -> {
                state.copy(localParticipantInfoModel = action.localParticipantInfoModel)
            }
            is ChatAction.Error -> {
                state.copy(chatStatus = ChatStatus.ERROR)
            }
            else -> state
        }
    }
}
