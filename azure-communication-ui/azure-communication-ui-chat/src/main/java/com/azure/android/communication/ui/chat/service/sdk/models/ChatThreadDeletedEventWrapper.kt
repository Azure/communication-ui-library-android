// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.chat.models.ChatParticipant
import org.threeten.bp.OffsetDateTime

internal interface ChatThreadDeletedEvent {
    val deletedOn: OffsetDateTime
    val deletedBy: ChatParticipant
}

internal class ChatThreadDeletedEventWrapper(private val chatThreadDeletedEvent: com.azure.android.communication.chat.models.ChatThreadDeletedEvent) :
    ChatThreadDeletedEvent, ChatEvent {
    override val deletedOn: OffsetDateTime = chatThreadDeletedEvent.deletedOn
    override val deletedBy: ChatParticipant = chatThreadDeletedEvent.deletedBy
    override val threadId: String = chatThreadDeletedEvent.chatThreadId
}
