// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.repository

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.NetworkAction
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
    private val messageRepository: MessageRepositoryMiddlewareInterface,
) :
    Middleware<ReduxState>,
    ChatMiddleware,
    RepositoryMiddleware {

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is ChatAction.SendMessage -> processSendMessage(action, store::dispatch)
                is ChatAction.MessagesPageReceived -> processPageReceived(action, store::dispatch)
                is ChatAction.MessageReceived -> processNewMessage(action, store::dispatch)
                is ChatAction.MessageDeleted -> processDeletedMessage(action, store::dispatch)
                is ChatAction.MessageEdited -> processEditMessage(action, store::dispatch)
                is NetworkAction.Disconnected -> processNetworkDisconnected(store::dispatch)
            }

            // Pass Action down the chain
            next(action)
        }
    }

    private fun processNetworkDisconnected(
        dispatch: Dispatch
    ) {
        messageRepository.getLastMessage()?.let { messageInfoModel ->
            val offsetDateTime = messageInfoModel.deletedOn ?: messageInfoModel.editedOn
                ?: messageInfoModel.createdOn
            offsetDateTime?.let {
                dispatch(NetworkAction.SetDisconnectedOffset(it))
            }
        }
    }

    private fun processNewMessage(
        action: ChatAction.MessageReceived,
        dispatch: Dispatch,
    ) {
        messageRepository.addServerMessage(action.message)
        notifyUpdate(dispatch)
    }

    private fun processPageReceived(
        action: ChatAction.MessagesPageReceived,
        dispatch: Dispatch,
    ) {
        messageRepository.addPage(action.messages.reversed())
        notifyUpdate(dispatch)
    }

    private fun processSendMessage(action: ChatAction.SendMessage, dispatch: Dispatch) {
        messageRepository.addLocalMessage(action.messageInfoModel)
        notifyUpdate(dispatch)
    }

    private fun processDeletedMessage(action: ChatAction.MessageDeleted, dispatch: Dispatch) {
        messageRepository.removeMessage(action.message)
        notifyUpdate(dispatch)
    }

    private fun processEditMessage(action: ChatAction.MessageEdited, dispatch: Dispatch) {
        messageRepository.editMessage(action.message)
        notifyUpdate(dispatch)
    }

    private fun notifyUpdate(dispatch: (Action) -> Unit) =
        dispatch(RepositoryAction.RepositoryUpdated())
}
