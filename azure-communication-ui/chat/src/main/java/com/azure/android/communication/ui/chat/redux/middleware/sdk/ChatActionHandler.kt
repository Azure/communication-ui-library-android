// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.error.ErrorCode
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.service.ChatService

// Converts Redux Actions into SDK Calls
// Redux -> Service
internal class ChatActionHandler(private val chatService: ChatService) {

    fun onAction(action: Action, dispatch: Dispatch) {
        when (action) {
            is ChatAction.StartChat -> initialization(dispatch = dispatch)
            is ChatAction.Initialized -> onChatInitialized(
                action = action,
                dispatch = dispatch
            )
            is ChatAction.SendMessage -> sendMessage(action = action, dispatch = dispatch)
        }
    }

    private fun sendMessage(action: ChatAction.SendMessage, dispatch: Dispatch) {
        chatService.sendMessage(action.messageInfoModel).whenComplete { result, error ->
            if (error != null) {
                dispatch(
                    ErrorAction.ChatStateErrorOccurred(
                        chatStateError = ChatStateError(
                            errorCode = ErrorCode.CHAT_SEND_MESSAGE_FAILED
                        )
                    )
                )
            } else {
                dispatch(
                    ChatAction.MessageSent(
                        messageInfoModel = action.messageInfoModel.copy(
                            id = result.id
                        )
                    )
                )
            }
        }
    }

    private fun initialization(dispatch: Dispatch) {
        try {
            chatService.initialize()
        } catch (ex: Exception) {
            val error = ChatStateError(errorCode = ErrorCode.CHAT_JOIN_FAILED)
            dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }
    }

    private fun onChatInitialized(action: ChatAction, dispatch: Dispatch) {
        // test code
        /*sendMessage(
            action = ChatAction.SendMessage(
                MessageInfoModel(
                    "123",
                    "456",
                    ChatMessageType.TEXT,
                    "hello"
                )
            ),
            dispatch = dispatch
        )*/
    }
}
