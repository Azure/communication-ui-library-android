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
import java.util.Collections

// Helper to share tests across implementations
internal class MessageRepositoryUnitTest {


    companion object {
    fun addPageTest(messageRepository: MessageRepository) {
        val messages = Collections.synchronizedList(mutableListOf<MessageInfoModel>())
        val numberOfTestMessages = 51
        for (i in 0..50) {
            messages.add(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        messageRepository.addPage(messages)

        val resultList = messageRepository.getSnapshotList()
        Assert.assertEquals(numberOfTestMessages, resultList.size)

        for (i in 0..50) {
            Assert.assertEquals("Message $i", resultList[i].content)
        }
    }


    fun removeMessageTest(messageRepository: MessageRepository) {
        val numberOfTestMessages = 51
        for (i in 0..numberOfTestMessages) {
            messageRepository.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }
        val resultList = messageRepository.getSnapshotList()
        messageRepository.removeMessage(resultList[0])

        val updatedList = messageRepository.getSnapshotList()
        Assert.assertEquals(numberOfTestMessages, updatedList.size)
    }

    @Test
    fun editMessageTest(messageRepository: MessageRepository) {
        val numberOfTestMessages = 51
        for (i in 0..numberOfTestMessages) {
            messageRepository.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        val resultList = messageRepository.getSnapshotList()

        val newMessage = MessageInfoModel(
            id = resultList[0].id,
            content = "Edited Message 0",
            messageType = resultList[0].messageType
        )

        messageRepository.editMessage(newMessage)

        val updatedResultList = messageRepository.getSnapshotList()
        Assert.assertEquals("Edited Message 0", updatedResultList[0].content)
    }

    @Test
    fun messageRepositoryListStorage_removeMessageTest(storage: MessageRepository) {
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

        storage.removeMessage(
            MessageInfoModel(
                id = "5",
                content = "Message $5",
                messageType = ChatMessageType.TEXT
            )
        )
        val resultList = storage.getSnapshotList()
        Assert.assertEquals(numberOfTestMessages - 1, resultList.size)
    }

    @Test
    fun outOfOrderTest(repository: MessageRepository) {


        // Add ID 4..7
        for (i in 4..7) {
            repository.addServerMessage(
                MessageInfoModel(
                    id = "$i",
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT,
                    createdOn = OffsetDateTime.of(2001, 3, 26, i, 0, i, 0, ZoneOffset.ofHours(2))
                )
            )
        }

        // Add ID 0 out of Order in middle
        repository.addServerMessage(
            MessageInfoModel(
                id = "0",
                content = "Message 0",
                messageType = ChatMessageType.TEXT,
                createdOn = OffsetDateTime.of(1980, 3, 26, 0, 0, 0, 0, ZoneOffset.ofHours(2))
            )
        )

        // Add IDs [1-3]
        for (i in 1..3) {
            repository.addServerMessage(
                MessageInfoModel(
                    id = "$i",
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT,
                    createdOn = OffsetDateTime.of(2000, 3, 26, i, 0, 0, 0, ZoneOffset.ofHours(2))
                )
            )
        }
        val resultList = repository.getSnapshotList()
        // Expect that first message is ID 0
        Assert.assertEquals("0", resultList[0].id)
        Assert.assertEquals("1", resultList[1].id)
        Assert.assertEquals("2", resultList[2].id)
        Assert.assertEquals("3", resultList[3].id)
        Assert.assertEquals("4", resultList[4].id)
        Assert.assertEquals("5", resultList[5].id)
        Assert.assertEquals("6", resultList[6].id)
        Assert.assertEquals("7", resultList[7].id)
    }

    @Test
    fun indexOfTest(storage: MessageRepository) {
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

        val resultList = storage.getSnapshotList()
        Assert.assertEquals(
            1,
            resultList.indexOf(
                MessageInfoModel(
                    id = "2",
                    content = "",
                    messageType = ChatMessageType.TEXT
                )
            )
        )
    }
    }
}
