// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

private val emptyMessage = MessageInfoModel(
    content = null,
    id = null,
    internalId = null,
    messageType = ChatMessageType.TEXT
)

internal class MessageRepository : List<MessageInfoModel> {
    override val size: Int = 0

    // TODO: Nothing here correctly implemented yet
    override fun contains(element: MessageInfoModel) = false
    override fun containsAll(elements: Collection<MessageInfoModel>) = false
    override fun get(index: Int) = emptyMessage
    override fun indexOf(element: MessageInfoModel) = 0
    override fun isEmpty() = true

    // Probably do not need Iterator methods, but they are on the interface
    override fun iterator(): Iterator<MessageInfoModel> {
        TODO("Not yet implemented")
    }

    override fun lastIndexOf(element: MessageInfoModel): Int {
        TODO("Not yet implemented")
    }

    override fun listIterator(): ListIterator<MessageInfoModel> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): ListIterator<MessageInfoModel> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<MessageInfoModel> {
        TODO("Not yet implemented")
    }
}
