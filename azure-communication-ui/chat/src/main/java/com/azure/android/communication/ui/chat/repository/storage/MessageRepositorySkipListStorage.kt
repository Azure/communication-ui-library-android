// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepositoryReader
import com.azure.android.communication.ui.chat.repository.MessageRepositoryWriter
import java.util.concurrent.ConcurrentSkipListMap

internal class MessageRepositorySkipListWriter : MessageRepositoryWriter {

    private val skipListStorage: ConcurrentSkipListMap<Long, MessageInfoModel> = ConcurrentSkipListMap()

    val size: Int
        get() = skipListStorage.size

    override fun addLocalMessage(messageInfoModel: MessageInfoModel) {
        val orderId: Long = getOrderId(messageInfoModel)
        skipListStorage.put(orderId, messageInfoModel)
    }

    override fun addPage(page: List<MessageInfoModel>) {
        page.forEach { it -> addLocalMessage(it) }
    }

    override fun addServerMessage(message: MessageInfoModel) {
        addLocalMessage(message)
    }

    override fun removeMessage(message: MessageInfoModel) {
        val orderId = getOrderId(message)

        if (skipListStorage.contains(orderId)) {
            skipListStorage.remove(orderId)
        }
    }

    override fun editMessage(message: MessageInfoModel) {
        val orderId = getOrderId(message)

        if (skipListStorage.contains(orderId)) {
            skipListStorage.get(orderId)?.let {
                mergeWithPreviousMessage(
                    it,
                    message
                )
            }?.let { skipListStorage.put(orderId, it) }
        } else {
            addLocalMessage(message)
        }
    }

    override fun getLastMessage(): MessageInfoModel? {
        val key = skipListStorage.lastKey()
        return skipListStorage.get(key)!!
    }

    fun searchItem(kth: Int): MessageInfoModel {

        var highestKey = skipListStorage.lastKey()
        var lowestKey = skipListStorage.firstKey()
        var elements = 0
        var midKey: Long = 0
        var items = kth
        while (lowestKey <= highestKey) {
            midKey = (highestKey + lowestKey).div(2)

            elements = skipListStorage.subMap(lowestKey, midKey + 1).size
            if (elements < items) {
                items -= elements
                lowestKey = midKey + 1
            } else if (elements > items) {
                highestKey = midKey - 1
            } else {
                break
            }
        }

        val key = skipListStorage.subMap(lowestKey, midKey + 1).lastKey()
        return skipListStorage.get(key)!!
    }

    private fun getOrderId(message: MessageInfoModel): Long {
        return message.id?.toLong() ?: 0L
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

internal class MessageRepositorySkipListReader(private val writer: MessageRepositorySkipListWriter) : MessageRepositoryReader() {

    override val size: Int
        get() = writer.size

    override fun get(index: Int): MessageInfoModel = try {
        writer.searchItem(index + 1)
    } catch (exception: Exception) {
        EMPTY_MESSAGE_INFO_MODEL
    }
}
