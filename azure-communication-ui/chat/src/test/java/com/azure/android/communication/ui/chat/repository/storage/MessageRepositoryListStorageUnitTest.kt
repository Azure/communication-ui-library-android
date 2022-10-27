// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.repository.MessageRepositoryType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.Collections

@RunWith(MockitoJUnitRunner::class)
internal class MessageRepositoryListStorageUnitTest {

    @Test
    fun messageRepositoryListStorage_addPage_test() {
        val messageRepository = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)

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

        Assert.assertEquals(numberOfTestMessages, messageRepository.size)

        for (i in 0..50) {
            Assert.assertEquals("Message $i", messageRepository[i].content)
        }
    }

    @Test
    fun messageRepositoryListStorage_removeMessage_test() {
        val messageRepository = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)

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

        messageRepository.removeMessage(messageRepository.get(0))

        Assert.assertEquals(numberOfTestMessages, messageRepository.size)
    }

    @Test
    fun messageRepositoryListStorage_editMessage_test() {
        val messageRepository = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)

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

        val newMessage = MessageInfoModel(
            id = messageRepository.get(0).id,
            content = "Edited Message 0",
            messageType = messageRepository.get(0).messageType
        )

        messageRepository.editMessage(newMessage)

        Assert.assertEquals("Edited Message 0", messageRepository.get(0).content)
    }

    @Test
    fun messageRepositoryListStorage_removeMessageTest() {
        val storage = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)

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

        Assert.assertEquals(numberOfTestMessages - 1, storage.size)
    }

    @Test
    fun messageRepositoryListStorage_OutOfOrderTest() {
        val repository = MessageRepository.getInstance(MessageRepositoryType.SYNCHRONIZED_LIST)

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

        // Expect that first message is ID 0
        Assert.assertEquals("0", repository[0].id)
        Assert.assertEquals("1", repository[1].id)
        Assert.assertEquals("2", repository[2].id)
        Assert.assertEquals("3", repository[3].id)
        Assert.assertEquals("4", repository[4].id)
        Assert.assertEquals("5", repository[5].id)
        Assert.assertEquals("6", repository[6].id)
        Assert.assertEquals("7", repository[7].id)
    }
}
