// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

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
    }

    fun unsubscribe() {
        coroutineScope.cancel()
    }
}
