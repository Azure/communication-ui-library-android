// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.error.ErrorCode
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// Listens to Events from SDK and Dispatches actions
// Service -> Redux
internal class ChatServiceListener(
    private val chatService: ChatService,
    coroutineContextProvider: CoroutineContextProvider,
) {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun subscribe(dispatch: Dispatch) {
        coroutineScope.launch {
            chatService.getChatStatusStateFlow().collect {
                when (it) {
                    ChatStatus.INITIALIZATION -> dispatch(ChatAction.Initialization())
                    ChatStatus.INITIALIZED -> dispatch(ChatAction.Initialized())
                    else -> {}
                }
            }
        }

        coroutineScope.launch {
            chatService.getMessagesPageSharedFlow().collect {
                onMessagesPageModelReceived(messagesPageModel = it, dispatch = dispatch)
            }
        }
    }

    fun unsubscribe() {
        coroutineScope.cancel()
    }

    private fun onMessagesPageModelReceived(
        messagesPageModel: MessagesPageModel,
        dispatch: Dispatch
    ) {

        messagesPageModel.throwable?.let {
            val error = ChatStateError(errorCode = ErrorCode.CHAT_FETCH_MESSAGES_FAILED)
            // TODO: lets use only one action and state to fire error for timing
            // TODO: while working on error stories, we can create separate states for every error
            dispatch(ErrorAction.ChatStateErrorOccurred(chatStateError = error))
        }

        messagesPageModel.messages?.let {
            dispatch(ChatAction.MessagesPageReceived(messages = it))
        }

        if (messagesPageModel.allPagesFetched) {
            dispatch(ChatAction.AllMessagesFetched())
        }
    }
}
