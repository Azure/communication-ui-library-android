// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListBackedImpl
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListBackedImpl
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeBackedImpl
import org.junit.Assert
import org.junit.Test

// Todo: Move these 3 tests to the 3 files in Storage package
// or add "verifyImplementation(messageRepo, readerClass, writerClass)" to main test file
// and delegate in the 3 files to verify concrete implementation is correctly chosen
class MessageRepositoryUnitTest {
    @Test
    fun messageRepository_createListStorage_unitTest() {
        val listStorage: MessageRepository = MessageRepository.createListBackedRepository()
        val messageStorageWriter = MessageRepositoryListBackedImpl()
        Assert.assertEquals(
            true,
            listStorage.javaClass.isInstance(messageStorageWriter),
        )
    }

    @Test
    fun messageRepository_createTreeStorage_unitTest() {
        val treeStorage: MessageRepository = MessageRepository.createTreeBackedRepository()
        val messageStorageWriter = MessageRepositoryTreeBackedImpl()
        Assert.assertEquals(
            true,
            treeStorage.javaClass.isInstance(messageStorageWriter),
        )
    }

    @Test
    fun messageRepository_createSkipListStorage_unitTest() {
        val skipListStorage: MessageRepository = MessageRepository.createSkipListBackedRepository()
        val messageStorageWriter = MessageRepositorySkipListBackedImpl()
        Assert.assertEquals(
            true,
            skipListStorage.javaClass.isInstance(messageStorageWriter),
        )
    }
}
