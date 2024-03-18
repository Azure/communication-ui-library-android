// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import android.util.Log
import com.azure.android.communication.chat.ChatClient
import com.azure.android.communication.chat.models.ChatEvent
import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.chat.models.ChatMessageReceivedEvent
import com.azure.android.communication.chat.models.TypingIndicatorReceivedEvent
import com.azure.android.communication.chat.models.ChatMessageEditedEvent
import com.azure.android.communication.chat.models.ChatMessageDeletedEvent
import com.azure.android.communication.chat.models.ReadReceiptReceivedEvent
import com.azure.android.communication.chat.models.ChatThreadDeletedEvent
import com.azure.android.communication.chat.models.ChatThreadPropertiesUpdatedEvent
import com.azure.android.communication.chat.models.ParticipantsAddedEvent
import com.azure.android.communication.chat.models.ParticipantsRemovedEvent
import com.azure.android.communication.ui.chat.models.into
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.ParticipantTimestampInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantsInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into

internal class ChatEventHandler {
    private val eventReceiver = this::onEventReceived
    private val messageReceivedEvent =
        ChatEventWrapper(ChatEventType.CHAT_MESSAGE_RECEIVED, eventReceiver)
    private val messageEditedEvent =
        ChatEventWrapper(ChatEventType.CHAT_MESSAGE_EDITED, eventReceiver)
    private val messageDeletedEvent =
        ChatEventWrapper(ChatEventType.CHAT_MESSAGE_DELETED, eventReceiver)
    private val typingIndicatorReceivedEvent =
        ChatEventWrapper(ChatEventType.TYPING_INDICATOR_RECEIVED, eventReceiver)
    private val readReceiptReceivedEvent =
        ChatEventWrapper(ChatEventType.READ_RECEIPT_RECEIVED, eventReceiver)
    private val chatThreadCreatedEvent =
        ChatEventWrapper(ChatEventType.CHAT_THREAD_CREATED, eventReceiver)
    private val chatThreadDeletedEvent =
        ChatEventWrapper(ChatEventType.CHAT_THREAD_DELETED, eventReceiver)
    private val chatThreadPropertiesUpdatedEvent =
        ChatEventWrapper(ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED, eventReceiver)
    private val participantAddedEvent =
        ChatEventWrapper(ChatEventType.PARTICIPANTS_ADDED, eventReceiver)
    private val participantRemovedEvent =
        ChatEventWrapper(ChatEventType.PARTICIPANTS_REMOVED, eventReceiver)

    private lateinit var chatThreadID: String
    private lateinit var localParticipantIdentifier: String
    private lateinit var eventSubscriber: (ChatEventModel) -> Unit

    fun start(
        chatClient: ChatClient,
        threadID: String,
        localParticipantIdentifier: String,
        eventSubscriber: (ChatEventModel) -> Unit,
    ) {
        this.chatThreadID = threadID
        this.eventSubscriber = eventSubscriber
        this.localParticipantIdentifier = localParticipantIdentifier

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
        if (chatThreadID != chatEvent.chatThreadId) {
            return
        }

        Log.d("Sanath testing", chatEvent.toString());
        when (eventType) {
            ChatEventType.CHAT_MESSAGE_RECEIVED -> {
                val event = chatEvent as ChatMessageReceivedEvent
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.CHAT_MESSAGE_RECEIVED.into(),
                    infoModel = event.into(localParticipantIdentifier),
                    eventReceivedOffsetDateTime = event.createdOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.CHAT_MESSAGE_EDITED -> {
                val event = chatEvent as ChatMessageEditedEvent
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.CHAT_MESSAGE_EDITED.into(),
                    infoModel = event.into(localParticipantIdentifier),
                    eventReceivedOffsetDateTime = event.editedOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.CHAT_MESSAGE_DELETED -> {
                val event = chatEvent as ChatMessageDeletedEvent
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.CHAT_MESSAGE_DELETED.into(),
                    infoModel = event.into(localParticipantIdentifier),
                    eventReceivedOffsetDateTime = event.deletedOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.TYPING_INDICATOR_RECEIVED -> {
                val event = chatEvent as TypingIndicatorReceivedEvent
                if (this.localParticipantIdentifier == event.sender.into().id) {
                    return
                }
                val model = ParticipantTimestampInfoModel(
                    userIdentifier = event.sender.into(),
                    receivedOn = event.receivedOn
                )
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.TYPING_INDICATOR_RECEIVED.into(),
                    infoModel = model,
                    eventReceivedOffsetDateTime = event.receivedOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.READ_RECEIPT_RECEIVED -> {
                val event = chatEvent as ReadReceiptReceivedEvent
                val model = ParticipantTimestampInfoModel(
                    userIdentifier = event.sender.into(),
                    receivedOn = event.readOn
                )
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.READ_RECEIPT_RECEIVED.into(),
                    infoModel = model,
                    eventReceivedOffsetDateTime = event.readOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.CHAT_THREAD_CREATED -> {
                // No use case
            }
            ChatEventType.CHAT_THREAD_DELETED -> {
                val event = chatEvent as ChatThreadDeletedEvent
                val model = ChatThreadInfoModel(receivedOn = event.deletedOn)
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.CHAT_THREAD_DELETED.into(),
                    infoModel = model,
                    eventReceivedOffsetDateTime = event.deletedOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED -> {
                val event = chatEvent as ChatThreadPropertiesUpdatedEvent
                val model = ChatThreadInfoModel(
                    receivedOn = event.updatedOn,
                    topic = event.properties.topic
                )
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED.into(),
                    infoModel = model,
                    eventReceivedOffsetDateTime = event.updatedOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.PARTICIPANTS_ADDED -> {
                val event = chatEvent as ParticipantsAddedEvent
                val model = RemoteParticipantsInfoModel(
                    participants = event.participantsAdded.map {
                        RemoteParticipantInfoModel(
                            userIdentifier = it.communicationIdentifier.into(),
                            displayName = it.displayName,
                            isLocalUser = it.communicationIdentifier.into().id == this.localParticipantIdentifier
                        )
                    }
                )
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.PARTICIPANTS_ADDED.into(),
                    infoModel = model,
                    eventReceivedOffsetDateTime = event.addedOn
                )
                eventSubscriber(infoModel)
            }
            ChatEventType.PARTICIPANTS_REMOVED -> {
                val event = chatEvent as ParticipantsRemovedEvent
                val model = RemoteParticipantsInfoModel(
                    participants = event.participantsRemoved.map {
                        RemoteParticipantInfoModel(
                            userIdentifier = it.communicationIdentifier.into(),
                            displayName = it.displayName,
                            isLocalUser = it.communicationIdentifier.into().id == this.localParticipantIdentifier
                        )
                    }
                )
                val infoModel = ChatEventModel(
                    eventType = ChatEventType.PARTICIPANTS_REMOVED.into(),
                    infoModel = model,
                    eventReceivedOffsetDateTime = event.removedOn
                )
                eventSubscriber(infoModel)
            }
        }
    }
}
