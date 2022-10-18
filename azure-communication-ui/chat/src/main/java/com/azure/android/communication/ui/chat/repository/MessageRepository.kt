// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import java.util.Collections

internal class MessageRepository : MessageRepositoryListInterface(), MessageRepositoryMiddlewareInterface {
    // Simple List for now
    private val messages = Collections.synchronizedList(mutableListOf<MessageInfoModel>())

    // Middleware Interface
    override fun addLocalMessage(messageInfoModel: MessageInfoModel) {
        messages.add(messageInfoModel)
        reorder()
    }

    override fun addPage(page: List<MessageInfoModel>) {
        messages.addAll(0, page)
        reorder()
    }

    override fun addServerMessage(message: MessageInfoModel) {
        messages.add(message)
        reorder()
    }

    override fun removeMessage(message: MessageInfoModel) {
        messages.retainAll { it.id != message.id }
    }

    override fun editMessage(message: MessageInfoModel) {
        val idx = messages.indexOfFirst {
            it.id == message.id
        }

        if (idx != -1) {
            // TODO: Merge with old message, keep metadata such as type. Update the old message with new message contents
            mergeWithPreviousMessage(idx, message)
        }
        reorder()
    }

    override fun getLastMessage(): MessageInfoModel? = messages?.last()

    override fun get(index: Int): MessageInfoModel = try {
        messages[index]
    } catch (exception: Exception) {
        EMPTY_MESSAGE_INFO_MODEL
    }

    override fun isEmpty() = messages.isEmpty()

    override val size: Int get() = messages.size

    fun reorder() {
        // TODO: Will need to update with repository stable algorithm implementation
        messages.sortBy {
            it.createdOn?.nano
        }
    }

    fun mergeWithPreviousMessage(idx: Int, message: MessageInfoModel) {
        var newMessage = MessageInfoModel(
            id = messages[idx].id,
            internalId = messages[idx].internalId,
            content = message.content,
            messageType = messages[idx].messageType,
            version = messages[idx].version,
            senderDisplayName = messages[idx].senderDisplayName,
            createdOn = messages[idx].createdOn,
            editedOn = messages[idx].editedOn,
            deletedOn = messages[idx].deletedOn,
            senderCommunicationIdentifier = messages[idx].senderCommunicationIdentifier
        )
        messages[idx] = newMessage
    }
}
