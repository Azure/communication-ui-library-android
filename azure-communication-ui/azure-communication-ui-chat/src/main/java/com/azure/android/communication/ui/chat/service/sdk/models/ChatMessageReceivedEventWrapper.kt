// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ChatMessageReceivedEvent {
    val type: ChatMessageType
    val content: String
    val id: String
    val senderDisplayName: String
    val createdOn: OffsetDateTime
    val version: String
    val metadata: Map<String, String>?
    val sender: CommunicationIdentifier
}

internal class ChatMessageReceivedEventWrapper(chatMessageReceivedEvent: com.azure.android.communication.chat.models.ChatMessageReceivedEvent) :
    ChatMessageReceivedEvent {
    override val type: ChatMessageType = chatMessageReceivedEvent.type
    override val content: String = chatMessageReceivedEvent.content
    override val id: String = chatMessageReceivedEvent.id
    override val senderDisplayName: String = chatMessageReceivedEvent.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessageReceivedEvent.createdOn
    override val version: String = chatMessageReceivedEvent.version
    override val metadata: Map<String, String>? = chatMessageReceivedEvent.metadata
    override val sender: CommunicationIdentifier = chatMessageReceivedEvent.sender.into()
}

internal class ChatMessageSdkEventWrapper(chatMessage: com.azure.android.communication.chat.models.ChatMessage) : ChatMessageReceivedEvent {
    override val type: ChatMessageType = chatMessage.type
    override val content: String = chatMessage.content.message
    override val id: String = chatMessage.id
    override val senderDisplayName: String = chatMessage.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessage.createdOn
    override val version: String = chatMessage.version
    override val metadata: Map<String, String>? = chatMessage.metadata
    override val sender: CommunicationIdentifier = chatMessage.senderCommunicationIdentifier.into()
}

internal class ChatMessageReceivedEventWrapperPolling(chatMessageReceivedEvent: com.azure.android.communication.chat.models.ChatMessage) :
    ChatMessageReceivedEvent {
    override val type: ChatMessageType = chatMessageReceivedEvent.type
    override val content: String = chatMessageReceivedEvent.content.message
    override val id: String = chatMessageReceivedEvent.id
    override val senderDisplayName: String = chatMessageReceivedEvent.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessageReceivedEvent.createdOn
    override val version: String = chatMessageReceivedEvent.version
    override val metadata: Map<String, String>? = chatMessageReceivedEvent.metadata
    override val sender: CommunicationIdentifier = chatMessageReceivedEvent.senderCommunicationIdentifier.into()
}
