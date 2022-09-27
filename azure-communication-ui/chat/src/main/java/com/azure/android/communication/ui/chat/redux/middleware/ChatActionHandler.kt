// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

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
            is ChatAction.StartChat -> initialization(dispatch)
            // is ChatAction.SendMessage -> sendMessage(action, dispatch)
        }
    }

    /* EXAMPLE:
    private fund sendMessage(action: ChatAction.SendMessage, dispatch) {
        try {
            chatService.sendMessage(action.message)
        } catch (ex: Exception) {
            val error = ChatStateError(errorCode = ErrorCode.SendMessageFailed)
            dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }
    }
     */

    private fun initialization(dispatch: Dispatch) {
        try {
            chatService.initialize()
        } catch (ex: Exception) {
            val error = ChatStateError(errorCode = ErrorCode.CHAT_JOIN_FAILED)
            dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }
    }
}
