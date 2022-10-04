// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.chat.ChatAsyncClient
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.ChatThreadAsyncClient
import com.azure.android.communication.chat.ChatThreadClientBuilder
import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.chat.models.ListChatMessagesOptions
import com.azure.android.communication.chat.models.SendChatMessageOptions
import com.azure.android.communication.chat.models.SendChatMessageResult
import com.azure.android.communication.chat.models.UpdateChatMessageOptions
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedPollingEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedPollingEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEventWrapperPolling
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipant
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadCreatedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadProperties
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadPropertiesWrapperPolling
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedPollingEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEventWrapperPolling
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRetrievedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ReadReceiptReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.TypingIndicatorReceivedEvent
import com.azure.android.core.http.policy.UserAgentPolicy
import com.azure.android.core.rest.Response
import com.azure.android.core.util.RequestContext
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

internal class ChatSDKWrapperPolling(
    private val pollingTime: Long,
    private val chatThreadData: ChatThreadData,
    private val chatEventsHandler: ChatEventsHandler,
) : ChatSDK, MessageStream {
    private lateinit var chatAsyncClient: ChatAsyncClient
    private lateinit var chatThreadAsyncClient: ChatThreadAsyncClient
    private var continuationToken: String? = null
    private var notificationsSubscribed = false
    private var subscribeMessageReceived = false
    private var subscribeMessageDeleted = false
    private var subscribeMessageEdited = false
    private var subscribeMessageTyping = false
    private var subscribeReadReceipt = false
    private var subscribeChatThreadCreated = false
    private var subscribeChatThreadDeleted = false
    private var subscribeChatThreadPropertiesUpdated = false
    private var subscribeParticipantAddedEvent = false
    private var subscribeParticipantRemovedEvent = false
    private var lastSyncTime: OffsetDateTime? = null
    private val queue = RingBuffer<String>(200)

    private fun processMessageListEveryMinute() {
        CoroutineScope(IO).launch {
            delay(pollingTime)
            CoroutineScope(Main).launch {
                val options = ListChatMessagesOptions()
                options.maxPageSize = 100
                lastSyncTime?.let {
                    options.startTime = lastSyncTime
                }
                val chatMessages = chatThreadAsyncClient.listMessages(options, RequestContext.NONE)

                chatMessages.byPage().forEach { pages ->
                    pages.elements.forEach { message ->

                        if (!queue.contents().contains(message.id + getLastTime(message))) {

                            queue.enqueue(message.id + getLastTime(message))

                            if (lastSyncTime == null || lastSyncTime?.isBefore(message.createdOn) == true) {
                                lastSyncTime = message.createdOn
                            }

                            if (message.editedOn != null) {
                                if (lastSyncTime == null || lastSyncTime?.isBefore(message.editedOn) == true) {
                                    lastSyncTime = message.editedOn
                                }
                            }

                            if (message.deletedOn != null) {
                                if (lastSyncTime == null || lastSyncTime?.isBefore(message.deletedOn) == true) {
                                    lastSyncTime = message.deletedOn
                                }
                            }

                            if (message.type == ChatMessageType.PARTICIPANT_ADDED) {
                                chatEventsHandler.onParticipantAdded(
                                    ParticipantsAddedPollingEventWrapper(message.createdOn, message.content.participants.map { it.into() })
                                )
                            }

                            if (message.type == ChatMessageType.PARTICIPANT_REMOVED) {
                                chatEventsHandler.onParticipantRemoved(
                                    ParticipantsRemovedEventWrapperPolling(message.createdOn, message.content.participants.map { it.into() })
                                )
                            }

                            if (message.type == ChatMessageType.HTML || message.type == ChatMessageType.TEXT) {

                                if (message.deletedOn != null) {
                                    chatEventsHandler.onMessageDeleted(
                                        ChatMessageDeletedPollingEventWrapper(message)
                                    )
                                }

                                if (message.editedOn != null) {
                                    chatEventsHandler.onMessageEdited(
                                        ChatMessageEditedPollingEventWrapper(message)
                                    )
                                }

                                if (message.deletedOn == null && message.editedOn == null) {
                                    chatEventsHandler.onMessageReceived(
                                        ChatMessageReceivedEventWrapperPolling(message)
                                    )
                                }
                            }

                            if (message.type == ChatMessageType.TOPIC_UPDATED) {
                                chatEventsHandler.onChatThreadPropertiesUpdated(
                                    ChatThreadPropertiesWrapperPolling(message.id, message.content.topic, message.createdOn)
                                )
                            }
                        }
                    }
                }
            }

            if (notificationsSubscribed) {
                processMessageListEveryMinute()
            }
        }
    }

    override fun getMessageByID(id: String): ChatMessage {
        return chatThreadAsyncClient.getMessage(id).get().into()
    }

    override fun sendMessage(type: ChatMessageType, content: String): CompletableFuture<Response<SendChatMessageResult>> {
        val chatMessageOptions = SendChatMessageOptions()
            .setType(type)
            .setContent(content)
            .setSenderDisplayName(chatThreadData.name)
        return chatThreadAsyncClient.sendMessageWithResponse(chatMessageOptions, RequestContext.NONE)
    }

    override fun sendTypingIndicator(): CompletableFuture<Response<Void>> {
        return chatThreadAsyncClient.sendTypingNotificationWithResponse(RequestContext.NONE)
    }

    override fun sendReadReceipt(id: String): CompletableFuture<Response<Void>> {
        return chatThreadAsyncClient.sendReadReceiptWithResponse(id, RequestContext.NONE)
    }

    override fun editMessage(id: String, content: String): CompletableFuture<Response<Void>> {
        val options = UpdateChatMessageOptions()
        options.content = content
        return chatThreadAsyncClient.updateMessageWithResponse(id, options, RequestContext.NONE)
    }

    override fun deleteMessage(id: String): CompletableFuture<Response<Void>> {
        return chatThreadAsyncClient.deleteMessageWithResponse(id, RequestContext.NONE)
    }

    override fun removeSelfFromChat(): CompletableFuture<Response<Void>> {
        return chatThreadAsyncClient.removeParticipantWithResponse(chatThreadData.identifier, RequestContext.NONE)
    }

    override fun fetchMessagesNextPage() {
        val options = ListChatMessagesOptions()
        options.maxPageSize = 2
        val chatMessages = chatThreadAsyncClient.listMessages(options, RequestContext.NONE)
        val chatStream = chatMessages.byPage(continuationToken)
        chatStream.forEach(MessagesStreamHandler(this)).cancel()
    }

    override fun getMessagesFirstPage() {
        val options = ListChatMessagesOptions()
        options.maxPageSize = 2
        val chatMessages = chatThreadAsyncClient.listMessages(options, RequestContext.NONE)
        val chatStream = chatMessages.byPage(continuationToken)
        chatStream.forEach(MessagesStreamHandler(this)).cancel()
    }

    override fun requestListOfParticipants() {
        val participant = listOf<ChatParticipant>()
        chatThreadAsyncClient.listParticipants().byPage().forEach {
            it.elements.forEach { chatParticipant ->
                participant.plus(chatParticipant.into())
            }
            chatEventsHandler.onParticipantsRetrieved(participant = participant)
        }
    }

    override fun getMessagesSharedFlow(): Flow<List<ChatMessage>> = chatEventsHandler.getMessagesSharedFlow()

    override fun getChatThreadCreatedEventSharedFlow(): Flow<ChatThreadCreatedEvent> =
        chatEventsHandler.getChatThreadCreatedEventSharedFlow()

    override fun getChatThreadDeletedEventSharedFlow(): Flow<ChatThreadDeletedEvent> =
        chatEventsHandler.getChatThreadDeletedEventSharedFlow()

    override fun getTypingIndicatorReceivedEventSharedFlow(): Flow<TypingIndicatorReceivedEvent> =
        chatEventsHandler.getTypingIndicatorReceivedEventSharedFlow()

    override fun getReadReceiptReceivedEventSharedFlow(): Flow<ReadReceiptReceivedEvent> =
        chatEventsHandler.getReadReceiptReceivedEventSharedFlow()

    override fun getParticipantsRetrievedEventSharedFlow(): Flow<ParticipantsRetrievedEvent> =
        chatEventsHandler.getParticipantsRetrievedEventSharedFlow()

    override fun getParticipantsRemovedEventSharedFlow(): Flow<ParticipantsRemovedEvent> =
        chatEventsHandler.getParticipantsRemovedEventSharedFlow()

    override fun getParticipantsAddedEventSharedFlow(): Flow<ParticipantsAddedEvent> =
        chatEventsHandler.getParticipantsAddedEventSharedFlow()

    override fun getChatMessageReceivedEventSharedFlow(): Flow<ChatMessageReceivedEvent> =
        chatEventsHandler.getChatMessageReceivedEventSharedFlow()

    override fun getChatMessageEditedEventSharedFlow(): Flow<ChatMessageEditedEvent> =
        chatEventsHandler.getChatMessageEditedEventSharedFlow()

    override fun getChatMessageDeletedEventSharedFlow(): Flow<ChatMessageDeletedEvent> =
        chatEventsHandler.getChatMessageDeletedEventSharedFlow()

    override fun getChatThreadPropertiesSharedFlow(): Flow<ChatThreadProperties> =
        chatEventsHandler.getChatThreadPropertiesSharedFlow()

    override fun createChatThreadClient() {
        chatThreadAsyncClient = ChatThreadClientBuilder()
            .endpoint(chatThreadData.url)
            .credential(chatThreadData.communicationTokenCredential)
            .addPolicy(
                UserAgentPolicy(
                    chatThreadData.applicationID,
                    chatThreadData.sdkName,
                    chatThreadData.sdkVersion
                )
            )
            .chatThreadId(chatThreadData.threadId)
            .buildAsyncClient()
    }

    override fun createChatClient() {
        chatAsyncClient = ChatClientBuilder()
            .endpoint(chatThreadData.url)
            .credential(chatThreadData.communicationTokenCredential)
            .addPolicy(
                UserAgentPolicy(
                    chatThreadData.applicationID,
                    chatThreadData.sdkName, chatThreadData.sdkVersion
                )
            )
            .buildAsyncClient()
    }

    override fun joinChatThread() {
        chatThreadAsyncClient = ChatThreadClientBuilder()
            .endpoint(chatThreadData.url)
            .credential(chatThreadData.communicationTokenCredential)
            .addPolicy(
                UserAgentPolicy(
                    chatThreadData.applicationID,
                    chatThreadData.sdkName, chatThreadData.sdkVersion
                )
            )
            .chatThreadId(chatThreadData.threadId)
            .buildAsyncClient()
    }

    override fun startRealTimeNotifications() {
        notificationsSubscribed = true
        processMessageListEveryMinute()
    }

    override fun stopRealTimeNotifications() {
        notificationsSubscribed = false
    }

    override fun addMessageReceivedEventHandler() {
        subscribeMessageReceived = true
    }

    override fun removeMessageReceivedEventHandler() {
        subscribeMessageReceived = false
    }

    override fun addMessageDeletedEventHandler() {
        subscribeMessageDeleted = true
    }

    override fun removeMessageDeletedEventHandler() {
        subscribeMessageDeleted = false
    }

    override fun addMessageEditedEventHandler() {
        subscribeMessageEdited = true
    }

    override fun removeMessageEditedEventHandler() {
        subscribeMessageEdited = false
    }

    override fun addTypingIndicatorReceivedEventHandler() {
        subscribeMessageTyping = true
    }

    override fun removeTypingIndicatorReceivedEventHandler() {
        subscribeMessageTyping = false
    }

    override fun addReadReceiptReceivedEventHandler() {
        subscribeReadReceipt = true
    }

    override fun removeReadReceiptReceivedEventHandler() {
        subscribeReadReceipt = false
    }

    override fun addChatThreadCreatedEventHandler() {
        subscribeChatThreadCreated = true
    }

    override fun removeChatThreadCreatedEventHandler() {
        subscribeChatThreadCreated = false
    }

    override fun addChatThreadDeletedEventHandler() {
        subscribeChatThreadDeleted = true
    }

    override fun removeChatThreadDeletedEventHandler() {
        subscribeChatThreadDeleted = false
    }

    override fun addChatThreadPropertiesUpdatedEventHandler() {
        subscribeChatThreadPropertiesUpdated = true
    }

    override fun removeChatThreadPropertiesUpdatedEventHandler() {
        subscribeChatThreadPropertiesUpdated = false
    }

    override fun addParticipantAddedEventHandler() {
        subscribeParticipantAddedEvent = true
    }

    override fun removeParticipantAddedEventHandler() {
        subscribeParticipantAddedEvent = false
    }

    override fun addParticipantRemovedEventHandler() {
        subscribeParticipantRemovedEvent = true
    }

    override fun removeParticipantRemovedEventHandler() {
        subscribeParticipantRemovedEvent = false
    }

    override fun setContinuationToken(token: String) {
        continuationToken = token
    }

    override fun setMessagesList(list: List<com.azure.android.communication.chat.models.ChatMessage>) {
        chatEventsHandler.messageList(list)
    }

    private fun getLastTime(message: com.azure.android.communication.chat.models.ChatMessage): String {
        if (message.deletedOn != null) return message.deletedOn.toString()
        if (message.editedOn != null) return message.editedOn.toString()

        return message.createdOn.toString()
    }
}

/**
 * RingBuffer uses a fixed length array to implement a queue, where,
 * - [tail] Items are added to the tail
 * - [head] Items are removed from the head
 * - [capacity] Keeps track of how many items are currently in the queue
 */
class RingBuffer<T>(val maxSize: Int = 10) {
    val array = mutableListOf<T?>().apply {
        for (index in 0 until maxSize) {
            add(null)
        }
    }

    // Head - remove from the head (read index)
    var head = 0

    // Tail - add to the tail (write index)
    var tail = 0

    // How many items are currently in the queue
    var capacity = 0

    fun clear() {
        head = 0
        tail = 0
    }

    fun enqueue(item: T): RingBuffer<T> {
        // Check if there's space before attempting to add the item
        if (capacity == maxSize) {
            dequeue()
        }

        array[tail] = item
        // Loop around to the start of the array if there's a need for it
        tail = (tail + 1) % maxSize
        capacity++

        return this
    }

    fun dequeue() {
        // Check if queue is empty before attempting to remove the item
        if (capacity != 0) {
            val result = array[head]
            // Loop around to the start of the array if there's a need for it
            head = (head + 1) % maxSize
            capacity--
        }
    }

    fun peek(): T? = array[head]

    /**
     * - Ordinarily, T > H ([isNormal]).
     * - However, when the queue loops over, then T < H ([isFlipped]).
     */
    fun isNormal(): Boolean {
        return tail > head
    }

    fun isFlipped(): Boolean {
        return tail < head
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
}
