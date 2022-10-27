// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryListWriter
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeReader
import com.azure.android.communication.ui.chat.repository.storage.MessageRepositoryTreeWriter
import org.junit.Assert
import org.junit.Test

class MessageRepositoryUnitTest {

    @Test
    fun messageRepository_createListStorage_unitTest() {
        val listStorage: MessageRepository = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)
        val messageStorageWriter = MessageRepositoryListWriter()
        Assert.assertEquals(true, listStorage.writerDelegate.javaClass.isInstance(messageStorageWriter))
        Assert.assertEquals(
            true,
            listStorage.readerDelegate.javaClass.isInstance(MessageRepositoryListReader(messageStorageWriter))
        )
        val newStorage: MessageRepository = MessageRepository.getInstance(MessageRepositoryType.TREEMAP)
        Assert.assertEquals(false, newStorage.writerDelegate.javaClass.isInstance(MessageRepositoryTreeWriter()))
        MessageRepository.tearDown()
    }

    @Test
    fun messageRepository_createTreeStorage_unitTest() {
        val treeStorage: MessageRepository = MessageRepository.getInstance(MessageRepositoryType.TREEMAP)
        val messageStorageWriter = MessageRepositoryTreeWriter()
        Assert.assertEquals(true, treeStorage.writerDelegate.javaClass.isInstance(messageStorageWriter))
        Assert.assertEquals(
            true,
            treeStorage.readerDelegate.javaClass.isInstance(MessageRepositoryTreeReader(messageStorageWriter))
        )
        val newStorage: MessageRepository = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)
        Assert.assertEquals(false, newStorage.writerDelegate.javaClass.isInstance(MessageRepositoryListWriter()))
        MessageRepository.tearDown()
    }
}
