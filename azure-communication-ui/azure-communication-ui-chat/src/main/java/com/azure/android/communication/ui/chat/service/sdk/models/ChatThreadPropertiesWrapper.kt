// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import org.threeten.bp.OffsetDateTime

internal interface ChatThreadProperties {
    val id: String
    val topic: String
    val createdOn: OffsetDateTime
}

internal class ChatThreadPropertiesWrapper(chatThreadCreatedEvent: com.azure.android.communication.chat.models.ChatThreadProperties) :
    ChatThreadProperties {
    override val id: String = chatThreadCreatedEvent.id
    override val topic: String = chatThreadCreatedEvent.topic
    override val createdOn: OffsetDateTime = chatThreadCreatedEvent.createdOn
}

internal class ChatThreadPropertiesWrapperPolling(
    messageID: String,
    messageTopic: String,
    messageCreatedOn: OffsetDateTime
) :
    ChatThreadProperties {
    override val id: String = messageID
    override val topic: String = messageTopic
    override val createdOn: OffsetDateTime = messageCreatedOn
}
