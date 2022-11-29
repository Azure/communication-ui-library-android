// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryList
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeStorage
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipList

internal class MessageRepository private constructor() {

    companion object {
        fun createListBackedRepository(): IMessageRepository {
            return MessageRepositoryList()
        }

        fun createTreeBackedRepository(): IMessageRepository {
            return MessageRepositoryTreeStorage()
        }

        fun createSkipListBackedRepository(): IMessageRepository {
            return  MessageRepositorySkipList()
        }
    }
}
