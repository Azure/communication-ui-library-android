// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.ui.chat.CoroutineContextProvider
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipant
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadCreatedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadProperties
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRetrievedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ReadReceiptReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.TypingIndicatorReceivedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal class ChatEventsHandler(coroutineContextProvider: CoroutineContextProvider) {

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    private var chatThreadCreatedEventSharedFlow: MutableSharedFlow<ChatThreadCreatedEvent> =
        MutableSharedFlow()
    private var chatThreadDeletedEventSharedFlow: MutableSharedFlow<ChatThreadDeletedEvent> =
        MutableSharedFlow()
    private var typingIndicatorReceivedEventSharedFlow: MutableSharedFlow<TypingIndicatorReceivedEvent> =
        MutableSharedFlow()
    private var readReceiptReceivedEventSharedFlow: MutableSharedFlow<ReadReceiptReceivedEvent> =
        MutableSharedFlow()
    private var participantsRemovedEventSharedFlow: MutableSharedFlow<ParticipantsRemovedEvent> =
        MutableSharedFlow()
    private var participantsAddedEventSharedFlow: MutableSharedFlow<ParticipantsAddedEvent> =
        MutableSharedFlow()
    private var participantsRetrievedEventSharedFlow: MutableSharedFlow<ParticipantsRetrievedEvent> =
        MutableSharedFlow()
    private var chatMessageReceivedEventSharedFlow: MutableSharedFlow<ChatMessageReceivedEvent> =
        MutableSharedFlow()
    private var chatMessageEditedEventSharedFlow: MutableSharedFlow<ChatMessageEditedEvent> =
        MutableSharedFlow()
    private var chatMessageDeletedEventSharedFlow: MutableSharedFlow<ChatMessageDeletedEvent> =
        MutableSharedFlow()
    private var chatThreadPropertiesSharedFlow: MutableSharedFlow<ChatThreadProperties> =
        MutableSharedFlow()
    private var chatMessagesSharedFlow: MutableSharedFlow<List<ChatMessage>> =
        MutableSharedFlow()

    fun getChatThreadCreatedEventSharedFlow(): Flow<ChatThreadCreatedEvent> =
        chatThreadCreatedEventSharedFlow

    fun getChatThreadDeletedEventSharedFlow(): Flow<ChatThreadDeletedEvent> =
        chatThreadDeletedEventSharedFlow

    fun getTypingIndicatorReceivedEventSharedFlow(): Flow<TypingIndicatorReceivedEvent> =
        typingIndicatorReceivedEventSharedFlow

    fun getReadReceiptReceivedEventSharedFlow(): Flow<ReadReceiptReceivedEvent> =
        readReceiptReceivedEventSharedFlow

    fun getParticipantsRemovedEventSharedFlow(): Flow<ParticipantsRemovedEvent> =
        participantsRemovedEventSharedFlow

    fun getParticipantsAddedEventSharedFlow(): Flow<ParticipantsAddedEvent> =
        participantsAddedEventSharedFlow

    fun getParticipantsRetrievedEventSharedFlow(): Flow<ParticipantsRetrievedEvent> =
        participantsRetrievedEventSharedFlow

    fun getChatMessageReceivedEventSharedFlow(): Flow<ChatMessageReceivedEvent> =
        chatMessageReceivedEventSharedFlow

    fun getChatMessageEditedEventSharedFlow(): Flow<ChatMessageEditedEvent> =
        chatMessageEditedEventSharedFlow

    fun getChatMessageDeletedEventSharedFlow(): Flow<ChatMessageDeletedEvent> =
        chatMessageDeletedEventSharedFlow

    fun getChatThreadPropertiesSharedFlow(): Flow<ChatThreadProperties> =
        chatThreadPropertiesSharedFlow

    fun onChatThreadCreated(event: ChatThreadCreatedEvent) {
        coroutineScope.launch {
            chatThreadCreatedEventSharedFlow.emit(event)
        }
    }

    fun onChatThreadDeleted(event: ChatThreadDeletedEvent) {
        coroutineScope.launch {
            chatThreadDeletedEventSharedFlow.emit(event)
        }
    }

    fun onTypingIndicatorReceived(event: TypingIndicatorReceivedEvent) {
        coroutineScope.launch {
            typingIndicatorReceivedEventSharedFlow.emit(event)
        }
    }

    fun onReadReceiptReceived(event: ReadReceiptReceivedEvent) {
        coroutineScope.launch {
            readReceiptReceivedEventSharedFlow.emit(event)
        }
    }

    fun onParticipantRemoved(event: ParticipantsRemovedEvent) {
        coroutineScope.launch {
            participantsRemovedEventSharedFlow.emit(event)
        }
    }

    fun onParticipantAdded(event: ParticipantsAddedEvent) {
        coroutineScope.launch {
            participantsAddedEventSharedFlow.emit(event)
        }
    }

    fun onMessageReceived(event: ChatMessageReceivedEvent) {
        coroutineScope.launch {
            chatMessageReceivedEventSharedFlow.emit(event)
        }
    }

    fun onMessageEdited(event: ChatMessageEditedEvent) {
        coroutineScope.launch {
            chatMessageEditedEventSharedFlow.emit(event)
        }
    }

    fun onMessageDeleted(event: ChatMessageDeletedEvent) {
        coroutineScope.launch {
            chatMessageDeletedEventSharedFlow.emit(event)
        }
    }

    fun onChatThreadPropertiesUpdated(event: ChatThreadProperties) {
        coroutineScope.launch {
            chatThreadPropertiesSharedFlow.emit(event)
        }
    }

    fun getMessagesSharedFlow(): Flow<List<ChatMessage>> = chatMessagesSharedFlow

    fun messageList(list: List<com.azure.android.communication.chat.models.ChatMessage>) {
        coroutineScope.launch {
            chatMessagesSharedFlow.emit(list.map { it.into() })
        }
    }

    fun onParticipantsRetrieved(participant: List<ChatParticipant>) {
        coroutineScope.launch {
            participantsRetrievedEventSharedFlow.emit(ParticipantsRetrievedEvent(participants = participant))
        }
    }
}
