// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.redux.middleware.listener.ChatActionHandler
import com.azure.android.communication.ui.chat.redux.middleware.listener.ChatServiceListener
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService

internal interface ChatServiceMiddleware

internal class ChatServiceServiceMiddlewareImpl(
    private val chatService: ChatService
) :
    Middleware<ReduxState>,
    ChatServiceMiddleware {

    private val chatActionHandler = ChatActionHandler(chatService = chatService)
    private val chatServiceListener = ChatServiceListener(chatService = chatService)

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->

            when (action) {
                is LifecycleAction.Wakeup -> {
                    chatService.init()
                    chatServiceListener.startListening(store::dispatch)
                }
                is LifecycleAction.Shutdown -> {
                    chatServiceListener.stopListening()
                    chatService.dispose()
                }
                else -> chatActionHandler.handleSendMessage(action, next)
            }
        }
    }

}
