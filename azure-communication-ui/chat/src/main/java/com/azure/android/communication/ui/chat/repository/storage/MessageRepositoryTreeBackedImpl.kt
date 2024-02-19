// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import java.util.TreeMap

internal class MessageRepositoryTreeBackedImpl : MessageRepository() {
    private val treeMapStorage: TreeMap<Long, MessageInfoModel> = TreeMap()

    override val size: Int
        get() = treeMapStorage.size

    override fun addMessage(messageInfoModel: MessageInfoModel) {
        val orderId: Long = messageInfoModel.normalizedID
        treeMapStorage[orderId] = messageInfoModel
    }

    override fun addPage(page: List<MessageInfoModel>) {
        page.forEach { addMessage(it) }
    }

    override fun removeMessage(message: MessageInfoModel) {
        val orderId = message.normalizedID

        if (treeMapStorage.contains(orderId)) {
            treeMapStorage.remove(orderId)
        }
    }

    private fun searchItem(kth: Int): MessageInfoModel {
        var highestKey = treeMapStorage.lastKey()
        var lowestKey = treeMapStorage.firstKey()
        var elements = 0
        var midKey: Long = 0
        var items = kth
        while (lowestKey <= highestKey) {
            midKey = (highestKey + lowestKey).div(2)

            elements = treeMapStorage.subMap(lowestKey, midKey + 1).size
            if (elements < items) {
                items -= elements
                lowestKey = midKey + 1
            } else if (elements > items) {
                highestKey = midKey - 1
            } else {
                break
            }
        }

        val key = treeMapStorage.subMap(lowestKey, midKey + 1).lastKey()
        return treeMapStorage.get(key)!!
    }

    fun searchIndexByID(messageId: Long): Int {
        var highestKey = treeMapStorage.lastKey()
        var lowestKey = treeMapStorage.firstKey()
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
        return treeMapStorage.headMap(midKey).size
    }

    override fun get(index: Int): MessageInfoModel =
        try {
            searchItem(index + 1)
        } catch (exception: Exception) {
            EMPTY_MESSAGE_INFO_MODEL
        }
}
