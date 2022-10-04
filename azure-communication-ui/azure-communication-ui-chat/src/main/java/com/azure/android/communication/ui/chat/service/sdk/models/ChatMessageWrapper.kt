// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ChatMessage {
    val id: String
    val type: ChatMessageType
    val version: String
    val content: String?
    val senderDisplayName: String?
    val createdOn: OffsetDateTime
    val senderCommunicationIdentifier: CommunicationIdentifier?
    val deletedOn: OffsetDateTime?
    val editedOn: OffsetDateTime?
}

internal class ChatMessageWrapper(chatMessage: com.azure.android.communication.chat.models.ChatMessage) : ChatMessage {
    override val id: String = chatMessage.id
    override val type: ChatMessageType = chatMessage.type
    override val version: String = chatMessage.version
    override val content: String? = chatMessage.content.message
    override val senderDisplayName: String? = chatMessage.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessage.createdOn
    override val senderCommunicationIdentifier: CommunicationIdentifier? = chatMessage.senderCommunicationIdentifier?.into()
    override val deletedOn: OffsetDateTime? = chatMessage.deletedOn
    override val editedOn: OffsetDateTime? = chatMessage.editedOn
}

internal class ChatMessageWrapperNewMessageEvent(chatMessageEvent: ChatMessageReceivedEvent) : ChatMessage {
    override val id: String = chatMessageEvent.id
    override val type: ChatMessageType = chatMessageEvent.type
    override val version: String = chatMessageEvent.version
    override val content: String? = chatMessageEvent.content
    override val senderDisplayName: String? = chatMessageEvent.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessageEvent.createdOn
    override val senderCommunicationIdentifier: CommunicationIdentifier? = null
    override val deletedOn: OffsetDateTime? = null
    override val editedOn: OffsetDateTime? = null
}
