// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import org.threeten.bp.OffsetDateTime

internal interface TypingIndicatorReceivedEvent {
    val version: String
    val receivedOn: OffsetDateTime
    val senderDisplayName: String
}

internal class TypingIndicatorReceivedEventWrapper(private val typingIndicatorReceivedEvent: com.azure.android.communication.chat.models.TypingIndicatorReceivedEvent) :
    TypingIndicatorReceivedEvent {
    override val version: String = typingIndicatorReceivedEvent.version
    override val receivedOn: OffsetDateTime = typingIndicatorReceivedEvent.receivedOn
    override val senderDisplayName: String = typingIndicatorReceivedEvent.senderDisplayName
}
