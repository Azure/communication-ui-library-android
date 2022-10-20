// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

@RunWith(MockitoJUnitRunner::class)
class MessageStorageUnitTest {

    @Test
    fun messageStorage_addPage_test() {

        val storage = MessageStorage()
        val numberOfTestMessages = 17
        for (i in 1..numberOfTestMessages) {
            storage.addLocalMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    createdOn = OffsetDateTime.of(2000, 3, 4, 5, i, 0, 0, ZoneOffset.ofHours(2)),
                    messageType = ChatMessageType.TEXT
                )
            )
        }

        Assert.assertEquals(numberOfTestMessages, storage.size)
        Assert.assertEquals("Message 4", storage.get(3).content)
    }
}
