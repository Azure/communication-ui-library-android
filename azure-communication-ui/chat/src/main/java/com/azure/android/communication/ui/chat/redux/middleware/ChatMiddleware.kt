// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.ErrorAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider

internal interface ChatMiddleware

internal class ChatMiddlewareImpl(
    chatService: ChatService,
    coroutineContextProvider: CoroutineContextProvider
) :
    Middleware<ReduxState>,
    ChatMiddleware {

    private val chatServiceListener = ChatServiceListener(chatService = chatService, coroutineContextProvider = coroutineContextProvider)
    private val chatActionHandler = ChatActionHandler(chatService =  chatService)

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is ChatAction.StartChat -> {
                    chatServiceListener.subscribe(store::dispatch)
                    chatActionHandler.initialization(store)
                }
                is ChatAction.Initialized -> {
                    chatActionHandler.initialized(store)
                }
                is ErrorAction.ChatStateErrorOccurred -> {
                    chatActionHandler.onError(store)
                }
            }
            next(action)
        }
    }
}
