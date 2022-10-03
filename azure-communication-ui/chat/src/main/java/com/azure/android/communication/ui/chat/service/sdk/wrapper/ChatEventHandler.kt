// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.wrapper

import com.azure.android.communication.chat.ChatClient
import com.azure.android.communication.chat.models.ChatEvent
import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.chat.models.ChatMessageReceivedEvent
import com.azure.android.communication.chat.models.TypingIndicatorReceivedEvent
import com.azure.android.communication.chat.models.ChatMessageEditedEvent
import com.azure.android.communication.chat.models.ChatMessageDeletedEvent
import com.azure.android.communication.chat.models.ReadReceiptReceivedEvent
import com.azure.android.communication.chat.models.ChatThreadCreatedEvent
import com.azure.android.communication.chat.models.ChatThreadDeletedEvent
import com.azure.android.communication.chat.models.ChatThreadPropertiesUpdatedEvent
import com.azure.android.communication.chat.models.ParticipantsAddedEvent
import com.azure.android.communication.chat.models.ParticipantsRemovedEvent

internal class ChatEventHandler {
    private val messageReceivedEvent =
        ChatEventWrapper(ChatEventType.CHAT_MESSAGE_RECEIVED, this::onEventReceived)
    private val messageEditedEvent =
        ChatEventWrapper(ChatEventType.CHAT_MESSAGE_EDITED, this::onEventReceived)
    private val messageDeletedEvent =
        ChatEventWrapper(ChatEventType.CHAT_MESSAGE_DELETED, this::onEventReceived)
    private val typingIndicatorReceivedEvent =
        ChatEventWrapper(ChatEventType.TYPING_INDICATOR_RECEIVED, this::onEventReceived)
    private val readReceiptReceivedEvent =
        ChatEventWrapper(ChatEventType.READ_RECEIPT_RECEIVED, this::onEventReceived)
    private val chatThreadCreatedEvent =
        ChatEventWrapper(ChatEventType.CHAT_THREAD_CREATED, this::onEventReceived)
    private val chatThreadDeletedEvent =
        ChatEventWrapper(ChatEventType.CHAT_THREAD_DELETED, this::onEventReceived)
    private val chatThreadPropertiesUpdatedEvent =
        ChatEventWrapper(ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED, this::onEventReceived)
    private val participantAddedEvent =
        ChatEventWrapper(ChatEventType.PARTICIPANTS_ADDED, this::onEventReceived)
    private val participantRemovedEvent =
        ChatEventWrapper(ChatEventType.PARTICIPANTS_REMOVED, this::onEventReceived)

    fun start(chatClient: ChatClient) {
        chatClient.addEventHandler(ChatEventType.CHAT_MESSAGE_RECEIVED, messageReceivedEvent)
        chatClient.addEventHandler(ChatEventType.CHAT_MESSAGE_EDITED, messageEditedEvent)
        chatClient.addEventHandler(ChatEventType.CHAT_MESSAGE_DELETED, messageDeletedEvent)
        chatClient.addEventHandler(
            ChatEventType.TYPING_INDICATOR_RECEIVED,
            typingIndicatorReceivedEvent
        )
        chatClient.addEventHandler(ChatEventType.READ_RECEIPT_RECEIVED, readReceiptReceivedEvent)
        chatClient.addEventHandler(ChatEventType.CHAT_THREAD_CREATED, chatThreadCreatedEvent)
        chatClient.addEventHandler(ChatEventType.CHAT_THREAD_DELETED, chatThreadDeletedEvent)
        chatClient.addEventHandler(
            ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
            chatThreadPropertiesUpdatedEvent
        )
        chatClient.addEventHandler(ChatEventType.PARTICIPANTS_ADDED, participantAddedEvent)
        chatClient.addEventHandler(ChatEventType.PARTICIPANTS_REMOVED, participantRemovedEvent)
    }

    fun stop(chatClient: ChatClient) {
        chatClient.removeEventHandler(ChatEventType.CHAT_MESSAGE_RECEIVED, messageReceivedEvent)
        chatClient.removeEventHandler(ChatEventType.CHAT_MESSAGE_EDITED, messageEditedEvent)
        chatClient.removeEventHandler(ChatEventType.CHAT_MESSAGE_DELETED, messageDeletedEvent)
        chatClient.removeEventHandler(
            ChatEventType.TYPING_INDICATOR_RECEIVED,
            typingIndicatorReceivedEvent
        )
        chatClient.removeEventHandler(ChatEventType.READ_RECEIPT_RECEIVED, readReceiptReceivedEvent)
        chatClient.removeEventHandler(ChatEventType.CHAT_THREAD_CREATED, chatThreadCreatedEvent)
        chatClient.removeEventHandler(ChatEventType.CHAT_THREAD_DELETED, chatThreadDeletedEvent)
        chatClient.removeEventHandler(
            ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
            chatThreadPropertiesUpdatedEvent
        )
        chatClient.removeEventHandler(ChatEventType.PARTICIPANTS_ADDED, participantAddedEvent)
        chatClient.removeEventHandler(ChatEventType.PARTICIPANTS_REMOVED, participantRemovedEvent)
    }

    private fun onEventReceived(eventType: ChatEventType, chatEvent: ChatEvent) {
        when (eventType) {
            ChatEventType.CHAT_MESSAGE_RECEIVED -> {
                val event = chatEvent as ChatMessageReceivedEvent
            }
            ChatEventType.CHAT_MESSAGE_EDITED -> {
                val event = chatEvent as ChatMessageEditedEvent
            }
            ChatEventType.CHAT_MESSAGE_DELETED -> {
                val event = chatEvent as ChatMessageDeletedEvent
            }
            ChatEventType.TYPING_INDICATOR_RECEIVED -> {
                val event = chatEvent as TypingIndicatorReceivedEvent
            }
            ChatEventType.READ_RECEIPT_RECEIVED -> {
                val event = chatEvent as ReadReceiptReceivedEvent
            }
            ChatEventType.CHAT_THREAD_CREATED -> {
                val event = chatEvent as ChatThreadCreatedEvent
            }
            ChatEventType.CHAT_THREAD_DELETED -> {
                val event = chatEvent as ChatThreadDeletedEvent
            }
            ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED -> {
                val event = chatEvent as ChatThreadPropertiesUpdatedEvent
            }
            ChatEventType.PARTICIPANTS_ADDED -> {
                val event = chatEvent as ParticipantsAddedEvent
            }
            ChatEventType.PARTICIPANTS_REMOVED -> {
                val event = chatEvent as ParticipantsRemovedEvent
            }
        }
    }
}
