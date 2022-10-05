// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.chat.ChatThreadClient
import com.azure.android.communication.chat.models.ChatMessage
import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.chat.models.ListChatMessagesOptions
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantsInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import com.azure.android.core.util.RequestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.asCoroutineDispatcher
import org.threeten.bp.OffsetDateTime
import java.util.concurrent.Executors

internal class ChatPollingHandler(coroutineContextProvider: CoroutineContextProvider) {
    companion object {
        private const val POLLING_INTERVAL = 5000L
    }

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private val singleThreadedContext = Executors.newSingleThreadExecutor()

    private lateinit var eventSubscriber: (ChatEventModel) -> Unit
    private lateinit var chatThreadClient: ChatThreadClient

    @Volatile
    private var lastMessageSyncTime: OffsetDateTime? = null

    private val listChatMessagesOptions = ListChatMessagesOptions().apply {
        maxPageSize = ChatSDKWrapper.PAGE_MESSAGES_SIZE
    }
    private val messagesRingBuffer = RingBuffer<String>(ChatSDKWrapper.PAGE_MESSAGES_SIZE * 2)

    // get messages after the latest received notification
    fun setLastMessageSyncTime(lastMessagesSyncTime: OffsetDateTime) {
        this.lastMessageSyncTime?.let {
            if (lastMessagesSyncTime < this.lastMessageSyncTime) return
        }
        this.lastMessageSyncTime = lastMessagesSyncTime
    }

    fun start(
        chatThreadClient: ChatThreadClient,
        eventSubscriber: (ChatEventModel) -> Unit
    ) {
        this.chatThreadClient = chatThreadClient
        this.eventSubscriber = eventSubscriber
        startPolling()
    }

    fun stop() {
        singleThreadedContext.shutdown()
        coroutineScope.cancel()
        messagesRingBuffer.clear()
    }

    private fun startPolling() {
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            withContext(singleThreadedContext.asCoroutineDispatcher()) {
                processMessagesList()
                delay(POLLING_INTERVAL)
                startPolling()
            }
        }
    }

    private fun getLastUpdatedTime(message: ChatMessage): String {
        if (message.deletedOn != null) return message.deletedOn.toString()
        if (message.editedOn != null) return message.editedOn.toString()
        return message.createdOn.toString()
    }

    private fun processMessagesList() {
        try {
            lastMessageSyncTime?.let {
                listChatMessagesOptions.startTime = lastMessageSyncTime
            }

            val messages =
                chatThreadClient.listMessages(listChatMessagesOptions, RequestContext.NONE)

            messages.byPage().forEach { pages ->
                pages.elements.forEach { message ->

                    // here ring bugger helps to
                    // make sure already notified message is not triggered as notification
                    // example: sending list messages request multiple time with same timestamp will return same last message
                    // the duplication of message is possible for only few last messages ths buffer size is equal to page size
                    // as the message ud remains same for edited/deleted message, thus
                    // to not ignore any updates, in buffer message id + last time stamp is stored
                    // in worst case the already notified messages will be notified back, if buffer limit is not enough
                    if (!messagesRingBuffer.contents()
                        .contains(message.id + getLastUpdatedTime(message))
                    ) {
                        messagesRingBuffer.enqueue(message.id + getLastUpdatedTime(message))

                        if (lastMessageSyncTime == null || lastMessageSyncTime?.isBefore(message.createdOn) == true) {
                            lastMessageSyncTime = message.createdOn
                        }

                        if (message.editedOn != null) {
                            if (lastMessageSyncTime == null || lastMessageSyncTime?.isBefore(message.editedOn) == true) {
                                lastMessageSyncTime = message.editedOn
                            }
                        }

                        if (message.deletedOn != null) {
                            if (lastMessageSyncTime == null || lastMessageSyncTime?.isBefore(message.deletedOn) == true) {
                                lastMessageSyncTime = message.deletedOn
                            }
                        }

                        if (message.type == ChatMessageType.PARTICIPANT_ADDED) {
                            val model = RemoteParticipantsInfoModel(
                                participants = message.content.participants.map {
                                    RemoteParticipantInfoModel(
                                        userIdentifier = it.communicationIdentifier.into(),
                                        displayName = it.displayName
                                    )
                                }
                            )
                            val infoModel = ChatEventModel(
                                eventType = ChatEventType.PARTICIPANTS_ADDED.into(),
                                infoModel = model,
                                eventReceivedOffsetDateTime = null
                            )
                            eventSubscriber(infoModel)
                        }

                        if (message.type == ChatMessageType.PARTICIPANT_REMOVED) {
                            val model = RemoteParticipantsInfoModel(
                                participants = message.content.participants.map {
                                    RemoteParticipantInfoModel(
                                        userIdentifier = it.communicationIdentifier.into(),
                                        displayName = it.displayName
                                    )
                                }
                            )
                            val infoModel = ChatEventModel(
                                eventType = ChatEventType.PARTICIPANTS_REMOVED.into(),
                                infoModel = model,
                                eventReceivedOffsetDateTime = null
                            )
                            eventSubscriber(infoModel)
                        }

                        if (message.type == ChatMessageType.HTML || message.type == ChatMessageType.TEXT) {
                            if (message.deletedOn != null) {
                                val model = MessageInfoModel(
                                    internalId = null,
                                    id = message.id,
                                    messageType = null,
                                    version = message.version,
                                    content = null,
                                    senderCommunicationIdentifier = message.content.initiatorCommunicationIdentifier.into(),
                                    senderDisplayName = message.senderDisplayName,
                                    createdOn = message.createdOn,
                                    deletedOn = message.deletedOn,
                                    editedOn = null
                                )
                                val infoModel = ChatEventModel(
                                    eventType = ChatEventType.CHAT_MESSAGE_DELETED.into(),
                                    infoModel = model,
                                    eventReceivedOffsetDateTime = null
                                )
                                eventSubscriber(infoModel)
                            }

                            if (message.editedOn != null) {
                                val model = MessageInfoModel(
                                    internalId = null,
                                    id = message.id,
                                    messageType = null,
                                    version = message.version,
                                    content = message.content.message,
                                    senderCommunicationIdentifier = message.content.initiatorCommunicationIdentifier.into(),
                                    senderDisplayName = message.senderDisplayName,
                                    createdOn = message.createdOn,
                                    deletedOn = null,
                                    editedOn = message.editedOn
                                )
                                val infoModel = ChatEventModel(
                                    eventType = ChatEventType.CHAT_MESSAGE_EDITED.into(),
                                    infoModel = model,
                                    eventReceivedOffsetDateTime = null
                                )
                                eventSubscriber(infoModel)
                            }

                            // new message
                            if (message.deletedOn == null && message.editedOn == null) {
                                val model = MessageInfoModel(
                                    internalId = null,
                                    id = message.id,
                                    messageType = message.type.into(),
                                    version = message.version,
                                    content = message.content.message,
                                    senderCommunicationIdentifier = message.content.initiatorCommunicationIdentifier.into(),
                                    senderDisplayName = message.senderDisplayName,
                                    createdOn = message.createdOn,
                                    deletedOn = null,
                                    editedOn = null
                                )
                                val infoModel = ChatEventModel(
                                    eventType = ChatEventType.CHAT_MESSAGE_RECEIVED.into(),
                                    infoModel = model,
                                    eventReceivedOffsetDateTime = null
                                )
                                eventSubscriber(infoModel)
                            }
                        }

                        if (message.type == ChatMessageType.TOPIC_UPDATED) {
                            val model = ChatThreadInfoModel(
                                receivedOn = message.editedOn ?: message.createdOn,
                                topic = message.content.topic
                            )
                            val infoModel = ChatEventModel(
                                eventType = ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED.into(),
                                infoModel = model,
                                eventReceivedOffsetDateTime = null
                            )
                            eventSubscriber(infoModel)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            // TODO: notify sdk wrapper about error in future
        }
    }

    /**
     * RingBuffer uses a fixed length array to implement a queue, where,
     * - [tail] Items are added to the tail
     * - [head] Items are removed from the head
     * - [capacity] Keeps track of how many items are currently in the queue
     */
    internal class RingBuffer<T>(private val maxSize: Int = 0) {
        private val array = mutableListOf<T?>().apply {
            for (index in 0 until maxSize) {
                add(null)
            }
        }

        var head = 0

        var tail = 0

        var capacity = 0

        fun clear() {
            head = 0
            tail = 0
        }

        fun enqueue(item: T): RingBuffer<T> {
            if (capacity == maxSize) {
                dequeue()
            }

            array[tail] = item
            tail = (tail + 1) % maxSize
            capacity++

            return this
        }

        fun contents(): MutableList<T?> {
            return mutableListOf<T?>().apply {
                var itemCount = capacity
                var readIndex = head
                while (itemCount > 0) {
                    add(array[readIndex])
                    readIndex = (readIndex + 1) % maxSize
                    itemCount--
                }
            }
        }

        private fun dequeue() {
            if (capacity != 0) {
                head = (head + 1) % maxSize
                capacity--
            }
        }
    }
}
