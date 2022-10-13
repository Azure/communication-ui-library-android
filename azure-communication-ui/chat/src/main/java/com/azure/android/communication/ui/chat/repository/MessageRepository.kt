// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import java.util.*

private val emptyMessage = MessageInfoModel(
    content = null,
    id = null,
    internalId = null,
    messageType = ChatMessageType.TEXT
)

// Interface for Message Repository Middleware to use
// I.e.
// - addLocalMessage
// - messageRetrieved,
// - pageRetrieved
// - messageEdited
// - messageDeleted
internal interface MessageRepositoryMiddlewareInterface {
    fun addLocalMessage(messageInfoModel: MessageInfoModel)
    fun addPage(page: List<MessageInfoModel>)
    fun addServerMessage(message: MessageInfoModel)
    fun removeMessage(message: MessageInfoModel)
    fun editMessage(message: MessageInfoModel)
}

internal class MessageRepository : List<MessageInfoModel>, MessageRepositoryMiddlewareInterface {
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

    // List Implementation
    // Important parts of a list to implement
    override val size get() = messages.size
    override fun indexOf(element: MessageInfoModel) = messages.indexOf(element)
    override fun get(index: Int): MessageInfoModel = try {
        messages[index]
    } catch (exception: Exception) {
        emptyMessage
    }

    override fun isEmpty() = messages.isEmpty()

    // Less Important, but should be easy to implement
    override fun contains(element: MessageInfoModel) = messages.contains(element)

    // Less or Not important parts of the List Interface
    // Don't hesitate to not support them if the internal implementation changes
    override fun containsAll(elements: Collection<MessageInfoModel>) =
        messages.containsAll(elements)

    override fun iterator(): Iterator<MessageInfoModel> = messages.iterator()
    override fun lastIndexOf(element: MessageInfoModel) = messages.lastIndexOf(element)
    override fun listIterator() = messages.listIterator()
    override fun listIterator(index: Int) = messages.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = messages.subList(fromIndex, toIndex)

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
