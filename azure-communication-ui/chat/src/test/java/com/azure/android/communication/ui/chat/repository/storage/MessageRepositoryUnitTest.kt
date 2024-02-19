// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.utilities.findMessageIdxById
import org.junit.Assert
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
                        messageType = ChatMessageType.TEXT,
                    ),
                )
            }

            messageRepository.addPage(messages)
            messageRepository.refreshSnapshot()
            val resultList = messageRepository.snapshotList
            Assert.assertEquals(numberOfTestMessages, resultList.size)

            for (i in 0..50) {
                Assert.assertEquals("Message $i", resultList[i].content)
            }
        }

        fun removeMessageTest(messageRepository: MessageRepository) {
            val numberOfTestMessages = 51
            for (i in 0..numberOfTestMessages) {
                messageRepository.addMessage(
                    MessageInfoModel(
                        id = i.toString(),
                        content = "Message $i",
                        messageType = ChatMessageType.TEXT,
                    ),
                )
            }
            messageRepository.refreshSnapshot()
            val resultList = messageRepository.snapshotList
            messageRepository.removeMessage(resultList[0])

            messageRepository.refreshSnapshot()
            val updatedList = messageRepository.snapshotList
            Assert.assertEquals(numberOfTestMessages, updatedList.size)
        }

        fun editMessageTest(messageRepository: MessageRepository) {
            val numberOfTestMessages = 51
            for (i in 0..numberOfTestMessages) {
                messageRepository.addMessage(
                    MessageInfoModel(
                        id = i.toString(),
                        content = "Message $i",
                        messageType = ChatMessageType.TEXT,
                    ),
                )
            }

            messageRepository.refreshSnapshot()
            val resultList = messageRepository.snapshotList

            val newMessage =
                MessageInfoModel(
                    id = resultList[0].normalizedID.toString(),
                    content = "Edited Message 0",
                    messageType = resultList[0].messageType,
                )

            messageRepository.replaceMessage(messageRepository.get(0), newMessage)
            messageRepository.refreshSnapshot()
            val updatedResultList = messageRepository.snapshotList
            Assert.assertEquals("Edited Message 0", updatedResultList[0].content)
        }

        fun messageRepositoryListStorage_removeMessageTest(storage: MessageRepository) {
            val numberOfTestMessages = 50
            for (i in 1..numberOfTestMessages) {
                storage.addMessage(
                    MessageInfoModel(
                        id = i.toString(),
                        content = "Message $i",
                        messageType = ChatMessageType.TEXT,
                    ),
                )
            }

            storage.removeMessage(
                MessageInfoModel(
                    id = "5",
                    content = "Message $5",
                    messageType = ChatMessageType.TEXT,
                ),
            )
            storage.refreshSnapshot()
            val resultList = storage.snapshotList
            Assert.assertEquals(numberOfTestMessages - 1, resultList.size)
        }

        fun outOfOrderTest(repository: MessageRepository) {
            // Add ID 4..7
            for (i in 4..7) {
                repository.addMessage(
                    MessageInfoModel(
                        id = "$i",
                        content = "Message $i",
                        messageType = ChatMessageType.TEXT,
                        createdOn = OffsetDateTime.of(2001, 3, 26, i, 0, i, 0, ZoneOffset.ofHours(2)),
                    ),
                )
            }

            // Add ID 0 out of Order in middle
            repository.addMessage(
                MessageInfoModel(
                    id = "0",
                    content = "Message 0",
                    messageType = ChatMessageType.TEXT,
                    createdOn = OffsetDateTime.of(1980, 3, 26, 0, 0, 0, 0, ZoneOffset.ofHours(2)),
                ),
            )

            // Add IDs [1-3]
            for (i in 1..3) {
                repository.addMessage(
                    MessageInfoModel(
                        id = "$i",
                        content = "Message $i",
                        messageType = ChatMessageType.TEXT,
                        createdOn = OffsetDateTime.of(2000, 3, 26, i, 0, 0, 0, ZoneOffset.ofHours(2)),
                    ),
                )
            }
            repository.refreshSnapshot()
            val resultList = repository.snapshotList
            // Expect that first message is ID 0
            Assert.assertEquals(0, resultList[0].normalizedID)
            Assert.assertEquals(1, resultList[1].normalizedID)
            Assert.assertEquals(2, resultList[2].normalizedID)
            Assert.assertEquals(3, resultList[3].normalizedID)
            Assert.assertEquals(4, resultList[4].normalizedID)
            Assert.assertEquals(5, resultList[5].normalizedID)
            Assert.assertEquals(6, resultList[6].normalizedID)
            Assert.assertEquals(7, resultList[7].normalizedID)
        }

        fun indexOfTest(storage: MessageRepository) {
            val numberOfTestMessages = 50
            for (i in 1..numberOfTestMessages) {
                storage.addMessage(
                    MessageInfoModel(
                        id = i.toString(),
                        content = "Message $i",
                        messageType = ChatMessageType.TEXT,
                    ),
                )
            }
            storage.refreshSnapshot()
            val resultList = storage.snapshotList
            Assert.assertEquals(
                1,
                resultList.findMessageIdxById(2),
            )
        }
    }
}
