// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListDelegate
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeStorageDelegate
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositorySkipListDelegate
import org.junit.Assert
import org.junit.Test

// Todo: Move these 3 tests to the 3 files in Storage package
// or add "verifyImplementation(messageRepo, readerClass, writerClass)" to main test file
// and delegate in the 3 files to verify concrete implementation is correctly chosen
class MessageRepositoryUnitTest {

    @Test
    fun messageRepository_createListStorage_unitTest() {
        val listStorage: IMessageRepository = IMessageRepository.createListBackedRepository()
        val messageStorageWriter = MessageRepositoryListDelegate()
        Assert.assertEquals(
            true,
            listStorage.delegate.javaClass.isInstance(messageStorageWriter)
        )
    }

    @Test
    fun messageRepository_createTreeStorage_unitTest() {
        val treeStorage: IMessageRepository = IMessageRepository.createTreeBackedRepository()
        val messageStorageWriter = MessageRepositoryTreeStorageDelegate()
        Assert.assertEquals(
            true,
            treeStorage.delegate.javaClass.isInstance(messageStorageWriter)
        )
    }

    @Test
    fun messageRepository_createSkipListStorage_unitTest() {
        val skipListStorage: IMessageRepository = IMessageRepository.createSkipListBackedRepository()
        val messageStorageWriter = MessageRepositorySkipListDelegate()
        Assert.assertEquals(
            true,
            skipListStorage.delegate.javaClass.isInstance(messageStorageWriter)
        )
    }
}
