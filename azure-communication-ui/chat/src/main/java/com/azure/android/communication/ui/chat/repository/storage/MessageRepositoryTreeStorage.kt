// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.INVALID_INDEX
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
        treeMapStorage[orderId] = messageInfoModel
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

        var highestKey = treeMapStorage.lastKey()
        var lowestKey = treeMapStorage.firstKey()
        var elements = 0
        var midKey: Long = 0
        var items = kth
        while (lowestKey <= highestKey) {
            midKey = (highestKey + lowestKey).div(2)

            elements = treeMapStorage.subMap(lowestKey, midKey + 1).size
            if (elements < items) {
                items -= elements
                lowestKey = midKey + 1
            } else if (elements > items) {
                highestKey = midKey - 1
            } else {
                break
            }
        }

        val key = treeMapStorage.subMap(lowestKey, midKey + 1).lastKey()
        return treeMapStorage.get(key)!!
    }

    fun searchIndexByID(messageId: Long): Int {
        var highestKey = treeMapStorage.lastKey()
        var lowestKey = treeMapStorage.firstKey()
        var midKey: Long = 0

        while (lowestKey <= highestKey) {
            midKey = (lowestKey + highestKey).div(2)

            if (messageId < midKey) {
                highestKey = midKey - 1
            } else if (messageId > midKey) {
                lowestKey = midKey + 1
            } else {
                break
            }
        }
        return treeMapStorage.headMap(midKey).size
    }

    private fun getOrderId(message: MessageInfoModel): Long {
        return message.id?.toLong() ?: 0L
    }

    private fun mergeWithPreviousMessage(
        previousMessage: MessageInfoModel,
        message: MessageInfoModel,
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

internal class MessageRepositoryTreeReader(private val writer: MessageRepositoryTreeWriter) :
    MessageRepositoryReader() {

    override val size: Int
        get() = writer.size

    override fun get(index: Int): MessageInfoModel = try {
        writer.searchItem(index + 1)
    } catch (exception: Exception) {
        EMPTY_MESSAGE_INFO_MODEL
    }

    override fun indexOf(element: MessageInfoModel): Int = try {
        writer.searchIndexByID(element.id!!.toLong())
    } catch (exception: Exception) {
        INVALID_INDEX
    }
}
