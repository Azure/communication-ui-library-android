// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import org.threeten.bp.OffsetDateTime

internal data class MessageInfoModel(
    val id: String?,
    val internalId: String? = null,
    val messageType: ChatMessageType?,
    val content: String?,
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
        internalId = null,
        version = this.version,
        senderDisplayName = this.senderDisplayName,
        createdOn = this.createdOn,
        senderCommunicationIdentifier = this.senderCommunicationIdentifier?.into(),
        deletedOn = this.deletedOn,
        editedOn = this.editedOn,
    )
}
