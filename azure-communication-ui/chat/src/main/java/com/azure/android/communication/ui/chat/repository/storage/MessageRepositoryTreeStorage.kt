// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepositoryReader
import com.azure.android.communication.ui.chat.repository.MessageRepositoryWriter
import java.util.TreeMap

internal class MessageRepositoryTreeWriter : MessageRepositoryWriter {

    private val treeMapStorage: TreeMap<Long, MessageInfoModel> = TreeMap()

    val size: Int
        get() = treeMapStorage.size

    override fun addLocalMessage(messageInfoModel: MessageInfoModel) {
        val orderId: Long = getOrderId(messageInfoModel)
        treeMapStorage.put(orderId, messageInfoModel)
    }

    override fun addPage(page: List<MessageInfoModel>) {
        page.forEach { it -> addLocalMessage(it) }
    }

    override fun addServerMessage(message: MessageInfoModel) {
        addLocalMessage(message)
    }

    override fun removeMessage(message: MessageInfoModel) {
        val orderId = getOrderId(message)

        if (treeMapStorage.contains(orderId)) {
            treeMapStorage.remove(orderId)
        }
    }

    override fun editMessage(message: MessageInfoModel) {
        val orderId = getOrderId(message)

        if (treeMapStorage.contains(orderId)) {
            treeMapStorage.get(orderId)?.let {
                mergeWithPreviousMessage(
                    it,
                    message
                )
            }?.let { treeMapStorage.put(orderId, it) }
        } else {
            addLocalMessage(message)
        }
    }

    override fun getLastMessage(): MessageInfoModel? {
        val key = treeMapStorage.lastKey()
        return treeMapStorage.get(key)!!
    }

    fun searchItem(kth: Int): MessageInfoModel {

        var highestKey = treeMapStorage.lastKey() + 1
        var lowestKey = treeMapStorage.firstKey()
        var elements = 0
        var midKey: Long = 0
        while (lowestKey <= highestKey) {
            midKey = (highestKey + lowestKey).div(2)

            elements = treeMapStorage.headMap(midKey).size

            if (elements < kth) {
                lowestKey = midKey + 1
            } else if (elements > kth) {
                highestKey = midKey - 1
            } else {
                break
            }
        }

        val key = treeMapStorage.headMap(midKey).lastKey()
        return treeMapStorage.get(key)!!
    }

    private fun getOrderId(message: MessageInfoModel): Long {
        return message.id?.toLong() ?: 0
    }

    private fun mergeWithPreviousMessage(
        previousMessage: MessageInfoModel,
        message: MessageInfoModel
    ): MessageInfoModel {
        var newMessage = MessageInfoModel(
            id = previousMessage.id,
            internalId = previousMessage.internalId,
            content = message.content,
            messageType = previousMessage.messageType,
            version = previousMessage.version,
            senderDisplayName = previousMessage.senderDisplayName,
            createdOn = previousMessage.createdOn,
            editedOn = previousMessage.editedOn,
            deletedOn = previousMessage.deletedOn,
            senderCommunicationIdentifier = previousMessage.senderCommunicationIdentifier,
            isCurrentUser = previousMessage.isCurrentUser,
        )
        return newMessage
    }
}

internal class MessageRepositoryTreeReader(private val writer: MessageRepositoryTreeWriter) : MessageRepositoryReader() {

    override val size: Int
        get() = writer.size

    override fun get(index: Int): MessageInfoModel = try {
        writer.searchItem(index + 1)
    } catch (exception: Exception) {
        EMPTY_MESSAGE_INFO_MODEL
    }
}
