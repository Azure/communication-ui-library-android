// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import android.content.Context
import com.azure.android.communication.chat.ChatAsyncClient
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.ChatThreadAsyncClient
import com.azure.android.communication.chat.ChatThreadClientBuilder
import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.chat.models.ListChatMessagesOptions
import com.azure.android.communication.chat.models.SendChatMessageOptions
import com.azure.android.communication.chat.models.SendChatMessageResult
import com.azure.android.communication.chat.models.UpdateChatMessageOptions
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageSdkEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipant
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadCreatedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadProperties
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRetrievedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ReadReceiptReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.TypingIndicatorReceivedEvent
import com.azure.android.core.http.policy.UserAgentPolicy
import com.azure.android.core.rest.Response
import com.azure.android.core.rest.util.paging.PagedResponse
import com.azure.android.core.util.AsyncStreamHandler
import com.azure.android.core.util.RequestContext
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow

internal class ChatSDKWrapper(
    private val context: Context,
    private val chatThreadData: ChatThreadData,
    private val chatEventsFactory: ChatEventsFactory,
    private val chatEventsHandler: ChatEventsHandler,
) : ChatSDK, MessageStream {
    private lateinit var chatAsyncClient: ChatAsyncClient
    private lateinit var chatThreadAsyncClient: ChatThreadAsyncClient
    private var continuationToken: String? = null

    override fun sendMessage(type: ChatMessageType, content: String): CompletableFuture<Response<SendChatMessageResult>> {
        val chatMessageOptions = SendChatMessageOptions()
            .setType(type)
            .setContent(content)
            .setSenderDisplayName(chatThreadData.name)
        return chatThreadAsyncClient.sendMessageWithResponse(chatMessageOptions, RequestContext.NONE)
    }

    override fun getMessageByID(id: String): ChatMessage {
        return chatThreadAsyncClient.getMessage(id).get().into()
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
        // val chatMessages = chatThreadAsyncClient.listMessages(options, RequestContext.NONE)
        // val chatStream = chatMessages.byPage(continuationToken)

        // chatStream.forEach(MessagesStreamHandler(this)).cancel()

        chatThreadAsyncClient.listMessages(options, RequestContext.NONE).byPage().forEach {
            it.elements.forEach { message ->
                chatEventsHandler.onMessageReceived(
                    ChatMessageSdkEventWrapper(message)
                )
            }
        }
    }

    override fun requestListOfParticipants() {

        var participantList = listOf<ChatParticipant>()
        chatThreadAsyncClient.listParticipants().byPage().forEach {
            it.elements.forEach { chatParticipant ->
                participantList = participantList.plus(chatParticipant.into())
            }
            chatEventsHandler.onParticipantsRetrieved(participantList)
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

    override fun getParticipantsRemovedEventSharedFlow(): Flow<ParticipantsRemovedEvent> =
        chatEventsHandler.getParticipantsRemovedEventSharedFlow()

    override fun getParticipantsAddedEventSharedFlow(): Flow<ParticipantsAddedEvent> =
        chatEventsHandler.getParticipantsAddedEventSharedFlow()

    override fun getParticipantsRetrievedEventSharedFlow(): Flow<ParticipantsRetrievedEvent> =
        chatEventsHandler.getParticipantsRetrievedEventSharedFlow()

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
        chatAsyncClient.startRealtimeNotifications(context) {
        }
    }

    override fun stopRealTimeNotifications() {
        chatAsyncClient.stopRealtimeNotifications()
    }

    override fun addMessageReceivedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.CHAT_MESSAGE_RECEIVED,
            chatEventsFactory.getMessageReceivedEvent()
        )
    }

    override fun removeMessageReceivedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.CHAT_MESSAGE_RECEIVED,
            chatEventsFactory.getMessageReceivedEvent()
        )
    }

    override fun addMessageDeletedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.CHAT_THREAD_DELETED,
            chatEventsFactory.getMessageDeletedEvent()
        )
    }

    override fun removeMessageDeletedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.CHAT_THREAD_DELETED,
            chatEventsFactory.getMessageDeletedEvent()
        )
    }

    override fun addMessageEditedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.CHAT_MESSAGE_EDITED,
            chatEventsFactory.getMessageEditedEvent()
        )
    }

    override fun removeMessageEditedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.CHAT_MESSAGE_EDITED,
            chatEventsFactory.getMessageEditedEvent()
        )
    }

    override fun addTypingIndicatorReceivedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.TYPING_INDICATOR_RECEIVED,
            chatEventsFactory.getTypingIndicatorEvent()
        )
    }

    override fun removeTypingIndicatorReceivedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.TYPING_INDICATOR_RECEIVED,
            chatEventsFactory.getTypingIndicatorEvent()
        )
    }

    override fun addReadReceiptReceivedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.READ_RECEIPT_RECEIVED,
            chatEventsFactory.getReadReceiptReceivedEvent()
        )
    }

    override fun removeReadReceiptReceivedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.READ_RECEIPT_RECEIVED,
            chatEventsFactory.getReadReceiptReceivedEvent()
        )
    }

    override fun addChatThreadCreatedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.CHAT_THREAD_CREATED,
            chatEventsFactory.getChatThreadCreated()
        )
    }

    override fun removeChatThreadCreatedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.CHAT_THREAD_CREATED,
            chatEventsFactory.getChatThreadCreated()
        )
    }

    override fun addChatThreadDeletedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.CHAT_THREAD_DELETED,
            chatEventsFactory.getChatThreadDeleted()
        )
    }

    override fun removeChatThreadDeletedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.CHAT_THREAD_DELETED,
            chatEventsFactory.getChatThreadDeleted()
        )
    }

    override fun addChatThreadPropertiesUpdatedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
            chatEventsFactory.getChatThreadPropertiesUpdated()
        )
    }

    override fun removeChatThreadPropertiesUpdatedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
            chatEventsFactory.getChatThreadPropertiesUpdated()
        )
    }

    override fun addParticipantAddedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.PARTICIPANTS_ADDED,
            chatEventsFactory.getParticipantAdded()
        )
    }

    override fun removeParticipantAddedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.PARTICIPANTS_ADDED,
            chatEventsFactory.getParticipantAdded()
        )
    }

    override fun addParticipantRemovedEventHandler() {
        chatAsyncClient.addEventHandler(
            ChatEventType.PARTICIPANTS_REMOVED,
            chatEventsFactory.getParticipantRemoved()
        )
    }

    override fun removeParticipantRemovedEventHandler() {
        chatAsyncClient.removeEventHandler(
            ChatEventType.PARTICIPANTS_REMOVED,
            chatEventsFactory.getParticipantRemoved()
        )
    }

    override fun setContinuationToken(token: String) {
        continuationToken = token
    }

    override fun setMessagesList(list: List<com.azure.android.communication.chat.models.ChatMessage>) {
        chatEventsHandler.messageList(list)
    }
}

internal interface MessageStream {
    fun setContinuationToken(token: String)
    fun setMessagesList(list: List<com.azure.android.communication.chat.models.ChatMessage>)
}

internal class MessagesStreamHandler(private val messageStream: MessageStream) : AsyncStreamHandler<PagedResponse<com.azure.android.communication.chat.models.ChatMessage>> {
    override fun onNext(e: PagedResponse<com.azure.android.communication.chat.models.ChatMessage>?) {
        e?.let {
            messageStream.setContinuationToken(it.continuationToken)
            it.elements?.let { list ->
                messageStream.setMessagesList(list)
            }
        }
    }
}
