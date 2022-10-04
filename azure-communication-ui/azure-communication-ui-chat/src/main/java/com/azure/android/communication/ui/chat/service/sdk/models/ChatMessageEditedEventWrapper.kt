// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.chat.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ChatMessageEditedEvent {
    val content: String
    val editedOn: OffsetDateTime
    val id: String
    val senderDisplayName: String
    val createdOn: OffsetDateTime
    val sender: CommunicationIdentifier
}

internal class ChatMessageEditedEventWrapper(private val chatMessageEditedEvent: com.azure.android.communication.chat.models.ChatMessageEditedEvent) :
    ChatMessageEditedEvent {
    override val content: String = chatMessageEditedEvent.content
    override val editedOn: OffsetDateTime = chatMessageEditedEvent.editedOn
    override val id: String = chatMessageEditedEvent.id
    override val senderDisplayName: String = chatMessageEditedEvent.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessageEditedEvent.createdOn
    override val sender: CommunicationIdentifier = chatMessageEditedEvent.sender.into()
}

internal class ChatMessageEditedPollingEventWrapper(message: ChatMessage) :
    ChatMessageEditedEvent {
    override val content: String = message.content.message
    override val editedOn: OffsetDateTime = message.editedOn
    override val id: String = message.id
    override val senderDisplayName: String = message.senderDisplayName
    override val createdOn: OffsetDateTime = message.createdOn
    override val sender: CommunicationIdentifier = message.senderCommunicationIdentifier.into()
}
