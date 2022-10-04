// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.chat.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ChatMessageDeletedEvent {
    val deletedOn: OffsetDateTime
    val id: String
    val senderDisplayName: String
    val createdOn: OffsetDateTime
    val sender: CommunicationIdentifier
}

internal class ChatMessageDeletedEventWrapper(chatMessageDeletedEvent: com.azure.android.communication.chat.models.ChatMessageDeletedEvent) :
    ChatMessageDeletedEvent {
    override val deletedOn: OffsetDateTime = chatMessageDeletedEvent.deletedOn
    override val id: String = chatMessageDeletedEvent.id
    override val senderDisplayName: String = chatMessageDeletedEvent.senderDisplayName
    override val createdOn: OffsetDateTime = chatMessageDeletedEvent.createdOn
    override val sender: CommunicationIdentifier = chatMessageDeletedEvent.sender.into()
}

internal class ChatMessageDeletedPollingEventWrapper(message: ChatMessage) :
    ChatMessageDeletedEvent {
    override val deletedOn: OffsetDateTime = message.deletedOn
    override val id: String = message.id
    override val senderDisplayName: String = message.senderDisplayName
    override val createdOn: OffsetDateTime = message.createdOn
    override val sender: CommunicationIdentifier = message.senderCommunicationIdentifier.into()
}
