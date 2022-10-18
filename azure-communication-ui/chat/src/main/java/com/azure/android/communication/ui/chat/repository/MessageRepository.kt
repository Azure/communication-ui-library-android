// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal class MessageRepository(
    val readerDelegate: MessageRepositoryReader,
    val writerDelegate: MessageRepositoryWriter) : MessageRepositoryReader(), MessageRepositoryWriter {

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
}
