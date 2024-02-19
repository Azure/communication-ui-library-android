// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.sdk

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal interface ChatMiddleware

// ChatMiddleware
//
// Manages
// ChatServiceListener (Service -> Redux)
// ChatActionHandler (Redux -> Service)
internal class ChatMiddlewareImpl(
    private val chatServiceListener: ChatServiceListener,
    private val chatActionHandler: ChatActionHandler,
) :
    Middleware<ReduxState>,
        ChatMiddleware {
    override fun invoke(store: Store<ReduxState>) =
        { next: Dispatch ->
            { action: Action ->
                // Handle Service Subscription/UnSubscription of service
                when (action) {
                    is ChatAction.StartChat -> {
                        chatServiceListener.subscribe(store)
                    }
                    is ChatAction.EndChat -> {
                        chatServiceListener.unsubscribe()
                    }
                }

                // Forward Actions to ChatActionHandler
                chatActionHandler.onAction(
                    action = action,
                    dispatch = store::dispatch,
                    state = store.getCurrentState(),
                )

                // Pass Action down the chain
                next(action)
            }
        }
}
