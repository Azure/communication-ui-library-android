// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepositoryReader
import com.azure.android.communication.ui.chat.repository.MessageRepositoryWriter
import java.util.Collections

internal class MessageRepositoryListWriter : MessageRepositoryWriter {
    // Simple List for now
    val messages: MutableList<MessageInfoModel> =
        Collections.synchronizedList(mutableListOf<MessageInfoModel>())

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

    fun reorder() {
        // TODO: Will need to update with repository stable algorithm implementation
        messages.sortBy {
            it.id?.toLong()
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
            senderCommunicationIdentifier = messages[idx].senderCommunicationIdentifier,
            isCurrentUser = messages[idx].isCurrentUser
        )
        messages[idx] = newMessage
    }
}

internal class MessageRepositoryListReader(private val writer: MessageRepositoryListWriter) :
    MessageRepositoryReader() {
    override fun get(index: Int): MessageInfoModel = try {
        writer.messages[index]
    } catch (exception: Exception) {
        EMPTY_MESSAGE_INFO_MODEL
    }

    override val size: Int get() = writer.messages.size

    override fun indexOf(element: MessageInfoModel): Int {
        val messageId = element.id!!.toLong()
        var index = 0
        for (message in writer.messages) {
            if (messageId == message.id!!.toLong()) {
                break
            }
            index++
        }
        return index
    }
}
