// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListWriter
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeWriter
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListWriter
import org.junit.Assert
import org.junit.Test

// Todo: Move these 3 tests to the 3 files in Storage package
// or add "verifyImplementation(messageRepo, readerClass, writerClass)" to main test file
// and delegate in the 3 files to verify concrete implementation is correctly chosen
class MessageRepositoryUnitTest {

    @Test
    fun messageRepository_createListStorage_unitTest() {
        val listStorage: MessageRepository = MessageRepository.createListBackedRepository()
        val messageStorageWriter = MessageRepositoryListWriter()
        Assert.assertEquals(
            true,
            listStorage.writerDelegate.javaClass.isInstance(messageStorageWriter)
        )
        Assert.assertEquals(
            true,
            listStorage.readerDelegate.javaClass.isInstance(
                MessageRepositoryListReader(
                    messageStorageWriter
                )
            )
        )
    }

    @Test
    fun messageRepository_createTreeStorage_unitTest() {
        val treeStorage: MessageRepository = MessageRepository.createTreeBackedRepository()
        val messageStorageWriter = MessageRepositoryTreeWriter()
        Assert.assertEquals(
            true,
            treeStorage.writerDelegate.javaClass.isInstance(messageStorageWriter)
        )
        Assert.assertEquals(
            true,
            treeStorage.readerDelegate.javaClass.isInstance(
                MessageRepositoryTreeReader(
                    messageStorageWriter
                )
            )
        )
    }

    @Test
    fun messageRepository_createSkipListStorage_unitTest() {
        val skipListStorage: MessageRepository = MessageRepository.createSkipListBackedRepository()
        val messageStorageWriter = MessageRepositorySkipListWriter()
        Assert.assertEquals(
            true,
            skipListStorage.writerDelegate.javaClass.isInstance(messageStorageWriter)
        )
        Assert.assertEquals(
            true,
            skipListStorage.readerDelegate.javaClass.isInstance(
                MessageRepositorySkipListReader(
                    messageStorageWriter
                )
            )
        )
    }
}
