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

@RunWith(MockitoJUnitRunner::class)
class MessageRepositoryTreeStorageUnitTest {

    @Test
    fun messageRepositoryTreeStorage_addMessage_test() {

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
        for (i in 1..numberOfTestMessages) {
            Assert.assertEquals("Message $i", storage[i - 1].content)
        }
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

        Assert.assertEquals(numberOfTestMessages - 1, storage.size)
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

    @Test
    fun messageRepositoryTreeStorage_editMessage_test() {
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

        storage.editMessage(
            MessageInfoModel(
                id = "5",
                content = "Message 55",
                messageType = ChatMessageType.TEXT
            )
        )

        Assert.assertEquals("Message 55", storage[4].content)
    }

    @Test
    fun messageRepositoryTreeStorage_addPage_test() {
        val storage = MessageRepository.createTreeBackedRepository()
        val numberOfTestMessages = 50
        val messageList = mutableListOf<MessageInfoModel>()
        for (i in 1..numberOfTestMessages) {
            messageList.add(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }
        storage.addPage(messageList)
        Assert.assertEquals(numberOfTestMessages, storage.size)
    }

    @Test
    fun messageRepositoryTreeStorage_PerformanceTest() {
        val storage = MessageRepository.createTreeBackedRepository()

        val startTime = System.nanoTime()

        // Increase decrease the number of messages to find out the execution time
        var numberOfTestMessages = 20000
        for (i in 1..numberOfTestMessages) {
            storage.addServerMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT,
                    topic = "chat",
                    participants = emptyList(),
                    senderDisplayName = "display name of sender"
                )
            )
        }

        // Edit Messages
        for (i in 1..numberOfTestMessages) {
            storage.editMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message ${i * 2}",
                    messageType = ChatMessageType.TEXT,
                    topic = "chat",
                    participants = emptyList(),
                    senderDisplayName = "display name of sender"
                )
            )
        }

        // delete messages
        for (i in 1..numberOfTestMessages) {
            storage.removeMessage(storage.get(storage.size - 1))
        }

        var endTime = System.nanoTime()
        var executionTime: Double = (endTime - startTime).toDouble().div(1000000000.0)

        println("---------- ExecutionTime ------------")
        println("Time: $executionTime")
        println("Time: ${endTime - startTime}")
        println("---------- ExecutionTime ------------")

        Assert.assertEquals(true, startTime <endTime)
    }

    @Test
    fun messageRepositoryTreeStorage_indexOfTest() {
        val storage = MessageRepository.createTreeBackedRepository()

        val numberOfTestMessages = 50
        for (i in 1..numberOfTestMessages) {
            storage.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        Assert.assertEquals(
            1,
            storage.indexOf(
                MessageInfoModel(
                    id = "2",
                    content = "",
                    messageType = ChatMessageType.TEXT
                )
            )
        )
    }
}
