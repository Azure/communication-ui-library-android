/*
 * *
 *  * Copyright (c) Microsoft Corporation. All rights reserved.
 *  * Licensed under the MIT License.
 *
 */

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepositoryReader
import com.azure.android.communication.ui.chat.repository.MessageRepositoryWriter
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import java.util.TreeMap
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

internal class MessageStorage : MessageRepositoryReader(), MessageRepositoryWriter {

    private val treeMapStoragePointer: TreeMap<Long, String> = TreeMap()
    private val treeMapStorage: TreeMap<String, MessageInfoModel> = TreeMap()

    override val size: Int
        get() = treeMapStorage.size

    override fun get(index: Int): MessageInfoModel {
        return searchItem(index+1)
    }

    override fun isEmpty(): Boolean {
        return treeMapStorage.isEmpty()
    }

    override fun addLocalMessage(messageInfoModel: MessageInfoModel) {
        val unixTime: Long = getUnixTime(messageInfoModel)

        messageInfoModel.id?.let { treeMapStoragePointer.put(unixTime, it) }

        messageInfoModel.id?.let { treeMapStorage.put(it, messageInfoModel) }
    }

    override fun addPage(page: List<MessageInfoModel>) {
        page.forEach { it -> addLocalMessage(it) }
    }

    override fun addServerMessage(message: MessageInfoModel) {
        addLocalMessage(message)
    }

    override fun removeMessage(message: MessageInfoModel) {
        val unixTime = getUnixTime(message)

        if (treeMapStoragePointer.contains(unixTime)) {
            treeMapStoragePointer.remove(unixTime)
            treeMapStorage.remove(message.id)
        }
    }

    override fun editMessage(message: MessageInfoModel) {
        val unixTime = getUnixTime(message)

        if (treeMapStoragePointer.contains(unixTime)) {
            treeMapStorage.get(message.id)?.let { mergeWithPreviousMessage(it, message) }
        } else {
            addLocalMessage(message)
        }
    }

    override fun getLastMessage(): MessageInfoModel? {
        TODO("Not yet implemented")
    }

    private fun getUnixTime(message: MessageInfoModel): Long {
        return message.createdOn?.toEpochSecond() ?: 0
    }

    private fun mergeWithPreviousMessage(previousMessage: MessageInfoModel,
                                         message: MessageInfoModel): MessageInfoModel {
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
            senderCommunicationIdentifier = previousMessage.senderCommunicationIdentifier
        )
        return newMessage
    }

    fun searchItem(kth: Int): MessageInfoModel {

        var highestKey = treeMapStoragePointer.lastKey()
        var lowestKey = treeMapStoragePointer.firstKey()
        var elements = 0
        var midKey: Long = 0
        while(lowestKey < highestKey) {
            midKey = (highestKey + lowestKey).div(2)

            elements = treeMapStoragePointer.headMap(midKey).size

            if(elements < kth) {
                lowestKey = midKey + 1
            } else if (elements > kth) {
                highestKey = midKey - 1
            } else {
                break
            }
        }

        val key = treeMapStoragePointer.headMap(midKey).lastKey()
        return treeMapStorage.get(treeMapStoragePointer.get(key))!!
    }


}
