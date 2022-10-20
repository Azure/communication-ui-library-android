// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

@RunWith(MockitoJUnitRunner::class)
class MessageRepositoryTreeStorageUnitTest {

    @Test
    fun messageRepositoryTreeStorage_addPage_test() {

        val storage = MessageRepository.createTreeBackedRepository()
        val numberOfTestMessages = 170
        for (i in 1..numberOfTestMessages) {
            storage.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        Assert.assertEquals(numberOfTestMessages, storage.size)
        for(i in 1..numberOfTestMessages) {
            Assert.assertEquals("Message $i", storage[i-1].content)
        }
        //Assert.assertEquals("Message 16", storage[15].content)
    }

    @Test
    fun messageRepositoryTreeStorage_removeMessage_test() {

        val storage = MessageRepository.createTreeBackedRepository()
        val numberOfTestMessages = 17
        for (i in 1..numberOfTestMessages) {
            storage.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        storage.removeMessage(
            MessageInfoModel(
                id = "17",
                content = "Message 17",
                messageType = ChatMessageType.TEXT
            )
        )

        Assert.assertEquals(numberOfTestMessages-1, storage.size)
        Assert.assertEquals(16, storage.getLastMessage()?.id?.toLong() ?: 0)
    }

    @Test
    fun messageRepositoryTreeStorage_getLastMessage_test() {
        val storage = MessageRepository.createTreeBackedRepository()
        val numberOfTestMessages = 17
        for (i in 1..numberOfTestMessages) {
            storage.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        storage.getLastMessage()?.id?.let { Assert.assertEquals(17, it.toLong()) }
    }
}