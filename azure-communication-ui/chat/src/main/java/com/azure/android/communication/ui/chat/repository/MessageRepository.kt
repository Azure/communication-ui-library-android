// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListBackedImpl
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListBackedImpl
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeBackedImpl
import kotlin.collections.ArrayList

// Abstract Class for Message Repository implementations

// I.e.
// - addLocalMessage
// - messageRetrieved,
// - pageRetrieved
// - messageEdited
// - messageDeleted
internal abstract class MessageRepository {
    abstract fun addPage(page: List<MessageInfoModel>)

    abstract fun addMessage(message: MessageInfoModel)

    abstract fun removeMessage(message: MessageInfoModel)

    abstract fun get(i: Int): MessageInfoModel

    abstract val size: Int

    private var currentSnapshot: List<MessageInfoModel> = listOf()
    val snapshotList get() = currentSnapshot

    fun replaceMessage(
        oldMessage: MessageInfoModel,
        newMessage: MessageInfoModel,
    ) {
        removeMessage(oldMessage)
        addMessage(newMessage)
    }

    // Simple Copy to build Snapshot for now
    // However, advised to implement a more efficient SnapshotList mechanism
    open fun buildSnapshotList(): List<MessageInfoModel> {
        // This is a inefficient implementation
        // but is generic and will work with any backing data
        val result = ArrayList<MessageInfoModel>()
        for (i in 0 until size) {
            result.add(get(i))
        }
        return result
    }

    // Refreshes the snapshot to match the current state of the repository
    // Call from Same thread as Repository Writes
    fun refreshSnapshot() {
        currentSnapshot = buildSnapshotList()
    }

    companion object {
        fun createListBackedRepository(): MessageRepository {
            return MessageRepositoryListBackedImpl()
        }

        fun createTreeBackedRepository(): MessageRepository {
            return MessageRepositoryTreeBackedImpl()
        }

        fun createSkipListBackedRepository(): MessageRepository {
            return MessageRepositorySkipListBackedImpl()
        }
    }
}
