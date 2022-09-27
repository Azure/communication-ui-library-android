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

internal interface ChatMiddleware

internal class ChatMiddlewareImpl(
    private val chatActionHandler: ChatActionHandler,
    private val chatServiceListener: ChatServiceListener
) :
    Middleware<ReduxState>,
    ChatMiddleware {
    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is ChatAction.startChat -> {
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
