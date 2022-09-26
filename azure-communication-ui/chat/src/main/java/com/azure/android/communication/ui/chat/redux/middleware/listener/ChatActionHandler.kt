// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

internal class ChatActionHandler(private val chatService: ChatService) {

    fun handleSendMessage(action: Action, next: Dispatch) {
        when (action) {
            is ChatAction.SendMessage -> handleSendMessage(action)
        }

        // Pass the action through to the next step
        next(action)
    }

    private fun handleSendMessage(action: ChatAction.SendMessage) {
        chatService.sendMessage(ChatMessageType.TEXT, action.message)
    }
}
