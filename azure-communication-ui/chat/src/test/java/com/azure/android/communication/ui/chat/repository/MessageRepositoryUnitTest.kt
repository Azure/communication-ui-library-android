// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import java.util.*

@RunWith(MockitoJUnitRunner::class)
internal class MessageRepositoryUnitTest {

    @Test
    fun messageRepository_addPage() {

        val messageRepository = MessageRepository()

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
            Assert.assertEquals("Message $i", messageRepository.get(i).content)
        }
    }

    @Test
    fun messageRepository_Reorder() {
        val messageRepository = MessageRepository()

        val messages = mutableListOf<MessageInfoModel>()
        val dateTime = mock<OffsetDateTime>()

        for (i in 1..3) {
            messages.add(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT,
                    createdOn = OffsetDateTime.of(2000, 3, 26, i, 0, 0, 0, ZoneOffset.ofHours(2))
                )
            )
        }
        messages.add(
            MessageInfoModel(
                id = "0",
                content = "Message 0",
                messageType = ChatMessageType.TEXT,
                createdOn = OffsetDateTime.of(1980, 3, 26, 0, 0, 0, 0, ZoneOffset.ofHours(2))
            )
        )
        for (i in 4..7) {
            messages.add(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT,
                    createdOn = OffsetDateTime.of(2001, 3, 26, i, 0, 0, 0, ZoneOffset.ofHours(2))
                )
            )
        }

        messageRepository.reorder()

        Assert.assertEquals("1", messages.get(0).id)
    }
}
