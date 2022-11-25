// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.storage.IMessageRepositoryListDelegate
import com.azure.android.communication.ui.chat.repository.storage.IMessageRepositoryTreeDelegate
import com.azure.android.communication.ui.chat.repository.storage.IMessageRepositorySkipListDelegate

internal class IMessageRepository private constructor(
    val writerDelegate: IMessageRepositoryDelegate,
) : IMessageRepositoryDelegate {

    override fun getSnapshotList(): List<MessageInfoModel> {
        return writerDelegate.getSnapshotList()
    }

    override fun get(i: Int) = writerDelegate.get(i)

    override val size: Int
        get() = writerDelegate.size

    override fun addPage(page: List<MessageInfoModel>) = writerDelegate.addPage(page)
    override fun addMessage(message: MessageInfoModel) = writerDelegate.addMessage(message)
    override fun removeMessage(message: MessageInfoModel) = writerDelegate.removeMessage(message = message)
    override fun replaceMessage(oldMessage: MessageInfoModel, newMessage: MessageInfoModel) = writerDelegate.replaceMessage(oldMessage, newMessage)

    companion object {

        fun createListBackedRepository(): IMessageRepository {
            val writer = IMessageRepositoryListDelegate()
            return IMessageRepository(
                writerDelegate = writer
            )
        }

        fun createTreeBackedRepository(): IMessageRepository {
            val writer = IMessageRepositoryTreeDelegate()
            return IMessageRepository(
                writerDelegate = writer
            )
        }

        fun createSkipListBackedRepository(): IMessageRepository {
            val writer = IMessageRepositorySkipListDelegate()
            return IMessageRepository(
                writerDelegate = writer
            )
        }
    }
}
