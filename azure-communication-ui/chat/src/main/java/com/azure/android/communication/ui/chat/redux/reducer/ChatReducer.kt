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
            is ChatAction.Initialization -> {
                state.copy(chatStatus = ChatStatus.INITIALIZATION)
            }
            is ChatAction.Initialized -> {
                state.copy(chatStatus = ChatStatus.INITIALIZED)
            }
            is ChatAction.TopicUpdated -> {
                state.copy(chatInfoModel = state.chatInfoModel.copy(topic = action.topic))
            }
            is ChatAction.AllMessagesFetched -> {
                state.copy(chatInfoModel = state.chatInfoModel.copy(allMessagesFetched = true))
            }
            is ChatAction.ThreadDeleted -> {
                state.copy(chatInfoModel = state.chatInfoModel.copy(isThreadDeleted = true))
            }
            is ChatAction.MessageRead -> {
                state.copy(
                    lastReadMessageId = if (state.lastReadMessageId.toInt() > action.messageId.toInt()) state.lastReadMessageId
                    else action.messageId
                )
            }
            else -> state
        }
    }
}
