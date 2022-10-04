// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.chat.models.SendChatMessageResult
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadCreatedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadProperties
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRetrievedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ReadReceiptReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.TypingIndicatorReceivedEvent
import com.azure.android.core.rest.Response
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow

internal interface ChatSDK {
    fun createChatThreadClient()
    fun createChatClient()
    fun joinChatThread()

    fun sendMessage(type: ChatMessageType, content: String): CompletableFuture<Response<SendChatMessageResult>>
    fun sendTypingIndicator(): CompletableFuture<Response<Void>>
    fun sendReadReceipt(id: String): CompletableFuture<Response<Void>>
    fun editMessage(id: String, content: String): CompletableFuture<Response<Void>>
    fun deleteMessage(id: String): CompletableFuture<Response<Void>>
    fun removeSelfFromChat(): CompletableFuture<Response<Void>>
    fun getMessageByID(id: String): ChatMessage

    fun fetchMessagesNextPage()
    fun getMessagesFirstPage()
    fun requestListOfParticipants()

    fun getMessagesSharedFlow(): Flow<List<ChatMessage>>
    fun getChatThreadCreatedEventSharedFlow(): Flow<ChatThreadCreatedEvent>
    fun getChatThreadDeletedEventSharedFlow(): Flow<ChatThreadDeletedEvent>
    fun getTypingIndicatorReceivedEventSharedFlow(): Flow<TypingIndicatorReceivedEvent>
    fun getReadReceiptReceivedEventSharedFlow(): Flow<ReadReceiptReceivedEvent>
    fun getParticipantsRetrievedEventSharedFlow(): Flow<ParticipantsRetrievedEvent>
    fun getParticipantsRemovedEventSharedFlow(): Flow<ParticipantsRemovedEvent>
    fun getParticipantsAddedEventSharedFlow(): Flow<ParticipantsAddedEvent>
    fun getChatMessageReceivedEventSharedFlow(): Flow<ChatMessageReceivedEvent>
    fun getChatMessageEditedEventSharedFlow(): Flow<ChatMessageEditedEvent>
    fun getChatMessageDeletedEventSharedFlow(): Flow<ChatMessageDeletedEvent>
    fun getChatThreadPropertiesSharedFlow(): Flow<ChatThreadProperties>

    fun startRealTimeNotifications()
    fun stopRealTimeNotifications()

    fun addMessageReceivedEventHandler()
    fun removeMessageReceivedEventHandler()

    fun addMessageDeletedEventHandler()
    fun removeMessageDeletedEventHandler()

    fun addMessageEditedEventHandler()
    fun removeMessageEditedEventHandler()

    fun addTypingIndicatorReceivedEventHandler()
    fun removeTypingIndicatorReceivedEventHandler()

    fun addReadReceiptReceivedEventHandler()
    fun removeReadReceiptReceivedEventHandler()

    fun addChatThreadCreatedEventHandler()
    fun removeChatThreadCreatedEventHandler()

    fun addChatThreadDeletedEventHandler()
    fun removeChatThreadDeletedEventHandler()

    fun addChatThreadPropertiesUpdatedEventHandler()
    fun removeChatThreadPropertiesUpdatedEventHandler()

    fun addParticipantAddedEventHandler()
    fun removeParticipantAddedEventHandler()

    fun addParticipantRemovedEventHandler()
    fun removeParticipantRemovedEventHandler()
}
