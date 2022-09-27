// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.error.ErrorCode
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService

internal class ChatActionHandler(private val chatService: ChatService) {
    fun initialization(store: Store<ReduxState>) {
        try {
            chatService.initialize()
        } catch (ex: Exception) {
            val error = ChatStateError(errorCode = ErrorCode.CHAT_JOIN_FAILED)
            store.dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }
    }

    fun initialized(store: Store<ReduxState>) {
        // TODO:subscribe to notifications
        val a = 4
    }

    fun onError(store: Store<ReduxState>) {
        // TODO:notify Contoso with Error
        // TODO:display UI discussion required
    }
}
