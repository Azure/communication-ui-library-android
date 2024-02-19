// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import java.util.concurrent.ConcurrentSkipListMap

internal class MessageRepositorySkipListBackedImpl : MessageRepository() {
    private val skipListStorage: ConcurrentSkipListMap<Long, MessageInfoModel> =
        ConcurrentSkipListMap()

    override val size: Int
        get() = skipListStorage.size

    override fun addMessage(messageInfoModel: MessageInfoModel) {
        val orderId: Long = messageInfoModel.normalizedID
        skipListStorage[orderId] = messageInfoModel
    }

    override fun addPage(page: List<MessageInfoModel>) {
        page.forEach { addMessage(it) }
    }

    override fun removeMessage(message: MessageInfoModel) {
        val orderId = message.normalizedID

        if (skipListStorage.contains(orderId)) {
            skipListStorage.remove(orderId)
        }
    }

    private fun searchItem(kth: Int): MessageInfoModel {
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

    fun searchIndexByID(messageId: Long): Int {
        var highestKey = skipListStorage.lastKey()
        var lowestKey = skipListStorage.firstKey()
        var midKey: Long = 0

        while (lowestKey <= highestKey) {
            midKey = (lowestKey + highestKey).div(2)

            if (messageId < midKey) {
                highestKey = midKey - 1
            } else if (messageId > midKey) {
                lowestKey = midKey + 1
            } else {
                break
            }
        }
        return skipListStorage.headMap(midKey).size
    }

    override fun get(index: Int): MessageInfoModel =
        try {
            searchItem(index + 1)
        } catch (exception: Exception) {
            EMPTY_MESSAGE_INFO_MODEL
        }
}
