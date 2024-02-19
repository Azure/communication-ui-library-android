// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.repository.MessageRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class MessageRepositoryListBackedImplStorageUnitTest {
    private fun getMessageRepo(): MessageRepository {
        return MessageRepository.createListBackedRepository()
    }

    @Test
    fun messageRepositoryListStorage_addPage_test() = MessageRepositoryUnitTest.addPageTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_removeMessage_test() = MessageRepositoryUnitTest.removeMessageTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_editMessage_test() = MessageRepositoryUnitTest.editMessageTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_removeMessageTest() = MessageRepositoryUnitTest.removeMessageTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_OutOfOrderTest() = MessageRepositoryUnitTest.outOfOrderTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_indexOfTest() = MessageRepositoryUnitTest.indexOfTest(getMessageRepo())
}
