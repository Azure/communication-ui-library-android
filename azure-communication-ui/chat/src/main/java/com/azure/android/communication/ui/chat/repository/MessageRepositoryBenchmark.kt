// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import android.util.Log
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

internal class MessageRepositoryBenchmark constructor(val type: MessageRepositoryType) {

    fun mockPerformance(messageCount: Int): Job = CoroutineScope(Dispatchers.IO).launch {
        val storage: MessageRepository = MessageRepository.getInstance(type)

        val startTime = System.nanoTime()
        // Adding message server

        var numberOfTestMessages = messageCount
        for (i in 1..numberOfTestMessages) {
            storage.addServerMessage(
                MessageInfoModel(
                    id = i.toString(),
                    content = "Message $i",
                    messageType = ChatMessageType.TEXT,
                    topic = "chat",
                    participants = emptyList(),
                    senderDisplayName = "display name of sender",
                    createdOn = OffsetDateTime.now(),
                    deletedOn = null,
                    editedOn = null
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
                    senderDisplayName = "display name of sender",
                    createdOn = OffsetDateTime.now(),
                    deletedOn = null,
                    editedOn = OffsetDateTime.now()
                )
            )
        }

        // delete messages
        for (i in 1..numberOfTestMessages) {
            storage.removeMessage(storage[i - 1])
        }

        var endTime = System.nanoTime()

        Log.d("IO Thread", "---------- ExecutionTime ------------")
        Log.d("IO Thread", "Time: ${endTime - startTime}")
        Log.d("IO Thread", "---------- ExecutionTime ------------")
    }
}
