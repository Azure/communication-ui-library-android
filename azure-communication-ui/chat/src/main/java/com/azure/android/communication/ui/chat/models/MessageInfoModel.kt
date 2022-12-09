// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.core.rest.annotation.Immutable
import org.threeten.bp.OffsetDateTime

internal enum class MessageSendStatus {
    SENDING, // default state, message is being sent
    SENT, // message is successfully sent
    SEEN, // read receipt for this message
    FAILED, // message failed to send
}

@Immutable
internal data class MessageInfoModel(
    private val id: String? = null,
    private val internalId: String? = null,
    val messageType: ChatMessageType? = null,
    val content: String? = null,
    val topic: String? = null,
    val participants: List<String> = emptyList(),
    val version: String? = null,
    val senderDisplayName: String? = null,
    val createdOn: OffsetDateTime? = null,
    val senderCommunicationIdentifier: CommunicationIdentifier? = null,
    val deletedOn: OffsetDateTime? = null,
    val editedOn: OffsetDateTime? = null,
    val sendStatus: MessageSendStatus? = null,
    val isCurrentUser: Boolean = false,
) : BaseInfoModel {
    // Normalized ID to use either internal or id
    internal val normalizedID: Long get() = id?.toLong() ?: internalId?.toLong() ?: 0L
//    internal val sendStatus: MessageSendStatus? = getSendStatus(id, internalId, isCurrentUser)
}

internal fun com.azure.android.communication.chat.models.ChatMessage.into(localParticipantIdentifier: String): MessageInfoModel {
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
        sendStatus = null,
        isCurrentUser = senderCommunicationIdentifier != null && localParticipantIdentifier == this.senderCommunicationIdentifier.into().id,
    )
}

internal fun com.azure.android.communication.chat.models.ChatMessageReceivedEvent.into(
    localParticipantIdentifier: String,
): MessageInfoModel {
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
        sendStatus = MessageSendStatus.SENT,
        isCurrentUser = localParticipantIdentifier == this.sender.into().id,
    )
}

internal fun com.azure.android.communication.chat.models.ChatMessageEditedEvent.into(
    localParticipantIdentifier: String,
): MessageInfoModel {
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
        sendStatus = MessageSendStatus.SENT,
        isCurrentUser = localParticipantIdentifier == this.sender.into().id,
    )
}

internal fun com.azure.android.communication.chat.models.ChatMessageDeletedEvent.into(
    localParticipantIdentifier: String,
): MessageInfoModel {
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
        sendStatus = null,
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
    sendStatus = MessageSendStatus.SENDING,
    isCurrentUser = false
)

// internal fun MessageInfoModel.getSendStatus(id: String?, internalId: String?, isCurrentUser: Boolean): MessageSendStatus? {
//
//    return if (isCurrentUser) {
//        if (id == null && internalId != null) {
//            MessageSendStatus.SENDING
//        } else if (id != null && internalId != null) {
//            MessageSendStatus.SENT
//        } else if (id != null && internalId == null) {
//            MessageSendStatus.SENT
//        }
//        else {
//            MessageSendStatus.FAILED
//        }
//    } else {
//        null
//    }
//            id == null && internalID != null = (sending)
//            id != null && internalId != null = (sent)
//            id != null ** internalId == null = (received)
// }

internal const val INVALID_INDEX = -1
