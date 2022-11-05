// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.core.rest.annotation.Immutable
import org.threeten.bp.OffsetDateTime

@Immutable
internal data class MessageInfoModel(
    val id: String?,
    val internalId: String? = null,
    val messageType: ChatMessageType?,
    val content: String?,
    val topic: String? = null,
    val participants: List<String> = emptyList(),
    val version: String? = null,
    val senderDisplayName: String? = null,
    val createdOn: OffsetDateTime? = null,
    val senderCommunicationIdentifier: CommunicationIdentifier? = null,
    val deletedOn: OffsetDateTime? = null,
    val editedOn: OffsetDateTime? = null,
    val isCurrentUser: Boolean = false,
) : BaseInfoModel

internal fun com.azure.android.communication.chat.models.ChatMessage.into(): MessageInfoModel {
    return MessageInfoModel(
        id = this.id,
        messageType = this.type.into(),
        content = this.content.message,
        topic = this.content.topic,
        participants = this.content.participants?.map { it.displayName }?.toList() ?: emptyList(),
        internalId = null,
        version = this.version,
        senderDisplayName = this.senderDisplayName,
        createdOn = this.createdOn,
        senderCommunicationIdentifier = this.senderCommunicationIdentifier?.into(),
        deletedOn = this.deletedOn,
        editedOn = this.editedOn,
    )
}

internal fun com.azure.android.communication.chat.models.ChatMessageReceivedEvent.into(localParticipantIdentifier: String): MessageInfoModel {
    return MessageInfoModel(
        internalId = null,
        id = this.id,
        messageType = this.type.into(),
        version = this.version,
        content = this.content,
        participants = emptyList(),
        senderCommunicationIdentifier = this.sender.into(),
        senderDisplayName = this.senderDisplayName,
        createdOn = this.createdOn,
        deletedOn = null,
        editedOn = null,
        isCurrentUser = localParticipantIdentifier == this.sender.into().id,
    )
}

internal fun com.azure.android.communication.chat.models.ChatMessageEditedEvent.into(localParticipantIdentifier: String): MessageInfoModel {
    return MessageInfoModel(
        internalId = null,
        id = this.id,
        messageType = null,
        version = this.version,
        content = this.content,
        participants = emptyList(),
        senderCommunicationIdentifier = this.sender.into(),
        senderDisplayName = this.senderDisplayName,
        createdOn = this.createdOn,
        deletedOn = null,
        editedOn = this.editedOn,
        isCurrentUser = localParticipantIdentifier == this.sender.into().id,
    )
}

internal fun com.azure.android.communication.chat.models.ChatMessageDeletedEvent.into(localParticipantIdentifier: String): MessageInfoModel {
    return MessageInfoModel(
        internalId = null,
        id = this.id,
        messageType = null,
        version = this.version,
        content = null,
        participants = emptyList(),
        senderCommunicationIdentifier = this.sender.into(),
        senderDisplayName = this.senderDisplayName,
        createdOn = this.createdOn,
        deletedOn = this.deletedOn,
        editedOn = null,
        isCurrentUser = localParticipantIdentifier == this.sender.into().id,
    )
}

internal val EMPTY_MESSAGE_INFO_MODEL = MessageInfoModel(
    id = "",
    messageType = null,
    content = "",
    participants = emptyList(),
    internalId = "",
    version = "",
    senderDisplayName = "",
    createdOn = OffsetDateTime.MIN,
    senderCommunicationIdentifier = null,
    deletedOn = null,
    editedOn = null,
    isCurrentUser = false
)

internal const val INVALID_INDEX = -1
