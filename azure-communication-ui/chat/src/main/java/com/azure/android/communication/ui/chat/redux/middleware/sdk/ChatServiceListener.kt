// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
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
            chatService.getMessageSharedFlow().collect {
                // TODO: remove test code
                /*it?.messages?.forEach { messageInfoModel ->
                    Log.d(
                        "helloh ",
                        messageInfoModel.content.toString() + " " + messageInfoModel.messageType
                    )
                }*/
            }
        }
    }

    fun unsubscribe() {
        coroutineScope.cancel()
    }
}
