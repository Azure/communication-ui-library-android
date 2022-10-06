// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.junit.Assert
import org.junit.Test

internal class MessageRepositoryUnitTest {

    @Test
    fun messageRepository_addBulkRemoteMessage() {

        val messageRepository = MessageRepository()

        val messages = mutableListOf<MessageInfoModel>()
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
}
