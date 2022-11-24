// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.action.NetworkAction
import com.azure.android.communication.ui.chat.redux.action.ParticipantAction
import com.azure.android.communication.ui.chat.redux.action.RepositoryAction
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatMiddleware
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.repository.MessageRepositoryWriter
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.threeten.bp.OffsetDateTime

internal interface MessageRepositoryMiddleware

// MessagesRepositoryMiddleware
//
// Manages
// ChatServiceListener (Service -> Redux)
// ChatActionHandler (Redux -> Service)
internal class MessageRepositoryMiddlewareImpl(
    private val messageRepository: MessageRepositoryWriter,
) :
    Middleware<ReduxState>,
    ChatMiddleware,
    MessageRepositoryMiddleware {

    override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
        { action: Action ->
            when (action) {
                is ChatAction.MessageSent -> processSentMessage(action, store::dispatch)
                is ChatAction.MessagesPageReceived -> processPageReceived(action, store::dispatch)
                is ChatAction.MessageReceived -> processNewMessage(action, store::dispatch)
                is ChatAction.MessageDeleted -> processDeletedMessage(action, store::dispatch)
                is ChatAction.MessageEdited -> processEditMessage(action, store::dispatch)
                is ParticipantAction.ParticipantsAdded -> processParticipantsAdded(
                    action,
                    store::dispatch
                )
                is ParticipantAction.ParticipantsRemoved -> processParticipantsRemoved(
                    action,
                    store::dispatch
                )
                is NetworkAction.Disconnected -> processNetworkDisconnected(store::dispatch)
            }

            // Pass Action down the chain
            next(action)
        }
    }

    private fun processNetworkDisconnected(
        dispatch: Dispatch,
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

    private fun processSentMessage(
        action: ChatAction.MessageSent,
        dispatch: Dispatch,
    ) {
        messageRepository.addLocalMessage(action.messageInfoModel)
        notifyUpdate(dispatch)
    }

    private fun processPageReceived(
        action: ChatAction.MessagesPageReceived,
        dispatch: Dispatch,
    ) {
        messageRepository.addPage(action.messages.reversed())
        notifyUpdate(dispatch)
    }

    var skipFirstParticipantsAddedMessage = true

    // Fake a message for Participant Added
    private fun processParticipantsAdded(
        action: ParticipantAction.ParticipantsAdded,
        dispatch: Dispatch,
    ) {
        // This comes through at start, but we don't want to pass it through
        // since it's also in the historical messages
        if (skipFirstParticipantsAddedMessage) {
            skipFirstParticipantsAddedMessage = false
            return
        }
        messageRepository.addLocalMessage(
            MessageInfoModel(
                id = "${messageRepository.getLastMessage()?.id?.toLong() ?: 0 + 1}",
                participants = action.participants.map { it.displayName ?: "" },
                content = null,
                createdOn = OffsetDateTime.now(),
                senderDisplayName = null,
                messageType = ChatMessageType.PARTICIPANT_ADDED
            )
        )
        notifyUpdate(dispatch)
    }

    private fun processParticipantsRemoved(
        action: ParticipantAction.ParticipantsRemoved,
        dispatch: Dispatch,
    ) {
        messageRepository.addLocalMessage(
            MessageInfoModel(
                id = "${messageRepository.getLastMessage()?.id?.toLong() ?: 0 + 1}",
                participants = action.participants.map { it.displayName ?: "" },
                content = null,
                createdOn = OffsetDateTime.now(),
                senderDisplayName = null,
                messageType = ChatMessageType.PARTICIPANT_REMOVED
            )
        )
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
