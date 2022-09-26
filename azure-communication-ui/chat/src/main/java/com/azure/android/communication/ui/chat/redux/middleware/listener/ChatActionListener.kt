// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.error.ErrorCode
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService

internal class ChatActionListener(private val chatService: ChatService) {
    fun initialization(store: Store<ReduxState>) {
        try {
            chatService.init()
        } catch (ex: Exception) {
            val error = ChatStateError(errorCode = ErrorCode.CHAT_JOIN_FAILED)
            store.dispatch(ChatAction.Error(chatStateError = error))
            store.dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }
    }

    fun initialized(store: Store<ReduxState>) {
        // TODO:subscribe to notifications
    }

    fun chatError(store: Store<ReduxState>) {
        // TODO:notify Contoso with Error
        // TODO:display UI discussion required
    }
}
