// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import java.util.Collections

internal class MessageRepositoryListBackedImpl : MessageRepository() {
    // Simple List for now
    val messages: MutableList<MessageInfoModel> =
        Collections.synchronizedList(mutableListOf<MessageInfoModel>())

    // Middleware Interface
    override fun addMessage(messageInfoModel: MessageInfoModel) {
        messages.add(messageInfoModel)
        reorder()
    }

    override fun addPage(page: List<MessageInfoModel>) {
        messages.addAll(0, page)
        reorder()
    }

    override fun removeMessage(message: MessageInfoModel) {
        messages.retainAll { it.normalizedID != message.normalizedID }
    }

    private fun reorder() {
        // TODO: Will need to update with repository stable algorithm implementation
        messages.sortBy {
            it.normalizedID
        }
    }

    override fun get(index: Int): MessageInfoModel =
        try {
            messages[index]
        } catch (exception: Exception) {
            EMPTY_MESSAGE_INFO_MODEL
        }

    override val size: Int get() = messages.size
}
