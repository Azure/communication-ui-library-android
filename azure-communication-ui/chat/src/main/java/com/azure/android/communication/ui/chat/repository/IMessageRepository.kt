// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListDelegate
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeStorageDelegate
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListDelegate

internal class IMessageRepository private constructor(
    val delegate: IMessageRepositoryDelegate,
) : IMessageRepositoryDelegate {

    @Synchronized override fun getSnapshotList(): List<MessageInfoModel> {
        return delegate.getSnapshotList()
    }

    override fun get(i: Int) = delegate.get(i)

    override val size: Int
        get() = delegate.size

    override fun addPage(page: List<MessageInfoModel>) = delegate.addPage(page)
    override fun addMessage(message: MessageInfoModel) = delegate.addMessage(message)
    override fun removeMessage(message: MessageInfoModel) = delegate.removeMessage(message = message)
    override fun replaceMessage(oldMessage: MessageInfoModel, newMessage: MessageInfoModel) = delegate.replaceMessage(oldMessage, newMessage)

    companion object {

        fun createListBackedRepository(): IMessageRepository {
            val writer = MessageRepositoryListDelegate()
            return IMessageRepository(
                delegate = writer
            )
        }

        fun createTreeBackedRepository(): IMessageRepository {
            val writer = MessageRepositoryTreeStorageDelegate()
            return IMessageRepository(
                delegate = writer
            )
        }

        fun createSkipListBackedRepository(): IMessageRepository {
            val writer = MessageRepositorySkipListDelegate()
            return IMessageRepository(
                delegate = writer
            )
        }
    }
}
