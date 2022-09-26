// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.middleware.listener.ChatActionListener
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal interface ChatMiddleware

internal class ChatMiddlewareImpl(
    private val chatActionListener: ChatActionListener
) :
    Middleware<ReduxState>,
    ChatMiddleware {
    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is ChatAction.Initialization -> {
                    chatActionListener.initialization(store)
                }
                is ChatAction.Initialized -> {
                    chatActionListener.initialized(store)
                }
                is ChatAction.Error -> {
                    chatActionListener.chatError(store)
                }
            }
            next(action)
        }
    }
}
