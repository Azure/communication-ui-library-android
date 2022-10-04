// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ChatThreadCreatedEvent {
    val chatThreadProperties: ChatThreadProperties
    var participants: List<ChatParticipant>
    var createdOn: OffsetDateTime
    var createdBy: ChatParticipant
}

internal class ChatThreadCreatedEventWrapper(chatThreadCreatedEvent: com.azure.android.communication.chat.models.ChatThreadCreatedEvent) :
    ChatThreadCreatedEvent {
    override val chatThreadProperties: ChatThreadProperties =
        chatThreadCreatedEvent.properties.into()
    override var participants: List<ChatParticipant> =
        chatThreadCreatedEvent.participants.map { it.into() }
    override var createdOn: OffsetDateTime = chatThreadCreatedEvent.createdOn
    override var createdBy: ChatParticipant = chatThreadCreatedEvent.createdBy.into()
}
