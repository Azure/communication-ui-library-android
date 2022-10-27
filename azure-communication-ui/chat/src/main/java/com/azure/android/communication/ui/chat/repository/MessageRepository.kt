// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListWriter
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeWriter
import org.jetbrains.annotations.TestOnly

internal enum class MessageRepositoryType {
    SYNCHRONIZED_LIST,
    TREEMAP,
    SKIP_LIST
}

internal class MessageRepository private constructor(
    val readerDelegate: MessageRepositoryReader,
    val writerDelegate: MessageRepositoryWriter
) : MessageRepositoryReader(), MessageRepositoryWriter {

    override val size: Int get() = readerDelegate.size
    override fun get(index: Int): MessageInfoModel = readerDelegate[index]
    override fun addLocalMessage(messageInfoModel: MessageInfoModel) = writerDelegate.addLocalMessage(messageInfoModel)
    override fun addPage(page: List<MessageInfoModel>) = writerDelegate.addPage(page)
    override fun addServerMessage(message: MessageInfoModel) = writerDelegate.addServerMessage(message = message)
    override fun removeMessage(message: MessageInfoModel) = writerDelegate.removeMessage(message = message)
    override fun editMessage(message: MessageInfoModel) = writerDelegate.editMessage(message = message)

    // TODO: We should be using read interface to get last message in list
    // This isn't a write message
    override fun getLastMessage(): MessageInfoModel? = writerDelegate.getLastMessage()

    companion object {

        private var instance: MessageRepository? = null

        fun getInstance(type: MessageRepositoryType): MessageRepository {

            return synchronized(this) {
                (
                    if (instance != null) {
                        instance
                    } else {
                        when (type) {
                            MessageRepositoryType.SYNCHRONIZED_LIST -> instance = createListBackedRepository()
                            MessageRepositoryType.TREEMAP -> instance = createTreeBackedRepository()
                            else -> {
                            }
                        }
                        instance
                    }
                    ) as MessageRepository
            }
        }

        @TestOnly
        fun tearDown() {
            instance = null
        }

        private fun createListBackedRepository(): MessageRepository {
            val writer = MessageRepositoryListWriter()
            val reader = MessageRepositoryListReader(writer)
            return MessageRepository(
                readerDelegate = reader,
                writerDelegate = writer
            )
        }

        private fun createTreeBackedRepository(): MessageRepository {
            val writer = MessageRepositoryTreeWriter()
            val reader = MessageRepositoryTreeReader(writer)
            return MessageRepository(
                readerDelegate = reader,
                writerDelegate = writer
            )
        }
    }
}
