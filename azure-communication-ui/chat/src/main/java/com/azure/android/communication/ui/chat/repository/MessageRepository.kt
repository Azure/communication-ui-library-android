// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

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
}

internal class MessageRepository : List<MessageInfoModel>, MessageRepositoryMiddlewareInterface {
    // Simple List for now
    private val messages = mutableListOf<MessageInfoModel>()

    // Middleware Interface
    override fun addLocalMessage(messageInfoModel: MessageInfoModel) {
        messages.add(messageInfoModel)
    }

    override fun addPage(page: List<MessageInfoModel>) {
        messages.addAll(0, page)
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
}
