// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.repository

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.RepositoryAction
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatMiddleware
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.repository.MessageRepositoryMiddlewareInterface

internal interface RepositoryMiddleware

// MessagesRepositoryMiddleware
//
// Manages
// ChatServiceListener (Service -> Redux)
// ChatActionHandler (Redux -> Service)
internal class RepositoryMiddlewareImpl(
    private val messageRepository: MessageRepositoryMiddlewareInterface
) :
    Middleware<ReduxState>,
    ChatMiddleware,
    RepositoryMiddleware {

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                // TODO: Map Actions from ChatServiceListener and UI to MessageRepo calls
                is ChatAction.SendMessage -> processSendMessage(action, store::dispatch)
                is ChatAction.DeleteMessage -> processDeleteMessage(action, store::dispatch)
            }

            // Pass Action down the chain
            next(action)
        }
    }

    private fun processSendMessage(action: ChatAction.SendMessage, dispatch: Dispatch) {
        messageRepository.addLocalMessage(action.messageInfoModel)
        notifyUpdate(dispatch)
    }

    private fun processDeleteMessage(action: ChatAction.DeleteMessage, dispatch: Dispatch) {
        messageRepository.deleteMessage(action.messageInfoModel)
        notifyUpdate(dispatch)
    }

    private fun notifyUpdate(dispatch: (Action) -> Unit) =
        dispatch(RepositoryAction.RepositoryUpdated())
}
