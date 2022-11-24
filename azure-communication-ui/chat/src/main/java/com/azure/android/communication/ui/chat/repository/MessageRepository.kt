// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListWriter
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeWriter
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListWriter

internal class MessageRepository private constructor(
    val readerDelegate: MessageRepositoryReader,
    val writerDelegate: MessageRepositoryWriter,
) : MessageRepositoryWriter {

    // override val size: Int get() = readerDelegate.size
    // override fun get(index: Int): MessageInfoModel = readerDelegate[index]
    // override fun indexOf(element: MessageInfoModel) = readerDelegate.indexOf(element)

    fun getSnapshotList(): List<MessageInfoModel> {
        return readerDelegate.getSnapshotList()
    }
    override fun addLocalMessage(messageInfoModel: MessageInfoModel) =
        writerDelegate.addLocalMessage(messageInfoModel)

    override fun addPage(page: List<MessageInfoModel>) = writerDelegate.addPage(page)
    override fun addServerMessage(message: MessageInfoModel) =
        writerDelegate.addServerMessage(message = message)

    override fun removeMessage(message: MessageInfoModel) =
        writerDelegate.removeMessage(message = message)

    override fun editMessage(message: MessageInfoModel) =
        writerDelegate.editMessage(message = message)

    // TODO: We should be using read interface to get last message in list
    // This isn't a write message
    override fun getLastMessage(): MessageInfoModel? = writerDelegate.getLastMessage()

    companion object {

        fun createListBackedRepository(): MessageRepository {
            val writer = MessageRepositoryListWriter()
            val reader = MessageRepositoryListReader(writer)
            return MessageRepository(
                readerDelegate = reader,
                writerDelegate = writer
            )
        }

        fun createTreeBackedRepository(): MessageRepository {
            val writer = MessageRepositoryTreeWriter()
            val reader = MessageRepositoryTreeReader(writer)
            return MessageRepository(
                readerDelegate = reader,
                writerDelegate = writer
            )
        }

        fun createSkipListBackedRepository(): MessageRepository {
            val writer = MessageRepositorySkipListWriter()
            val reader = MessageRepositorySkipListReader(writer)
            return MessageRepository(
                readerDelegate = reader,
                writerDelegate = writer
            )
        }
    }
}
