// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessage
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageDeletedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageEditedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageReceivedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatMessageWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipant
import com.azure.android.communication.ui.chat.service.sdk.models.ChatParticipantWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadCreatedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadCreatedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadDeletedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadDeletedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadProperties
import com.azure.android.communication.ui.chat.service.sdk.models.ChatThreadPropertiesWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsAddedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ParticipantsRemovedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.ReadReceiptReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.ReadReceiptReceivedEventWrapper
import com.azure.android.communication.ui.chat.service.sdk.models.TypingIndicatorReceivedEvent
import com.azure.android.communication.ui.chat.service.sdk.models.TypingIndicatorReceivedEventWrapper

internal fun com.azure.android.communication.chat.models.ChatThreadCreatedEvent.into(): ChatThreadCreatedEvent {
    return ChatThreadCreatedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ChatThreadProperties.into(): ChatThreadProperties {
    return ChatThreadPropertiesWrapper(this)
}

internal fun com.azure.android.communication.common.CommunicationIdentifier.into(): CommunicationIdentifier {
    return when (this) {
        is CommunicationUserIdentifier -> CommunicationIdentifier.CommunicationUserIdentifier(this.id)
        is MicrosoftTeamsUserIdentifier -> CommunicationIdentifier.MicrosoftTeamsUserIdentifier(
            this.userId,
            this.isAnonymous
        )
        is PhoneNumberIdentifier -> CommunicationIdentifier.PhoneNumberIdentifier(this.phoneNumber)
        is UnknownIdentifier -> CommunicationIdentifier.UnknownIdentifier(this.id)
        else -> {
            throw IllegalStateException("Unknown type of CommunicationIdentifier: $this")
        }
    }
}

internal fun CommunicationIdentifier.into(): com.azure.android.communication.common.CommunicationIdentifier {
    return when (this) {
        is CommunicationIdentifier.CommunicationUserIdentifier -> CommunicationUserIdentifier(this.userId)
        is CommunicationIdentifier.MicrosoftTeamsUserIdentifier -> MicrosoftTeamsUserIdentifier(
            this.userId,
            this.isAnonymous
        )
        is CommunicationIdentifier.PhoneNumberIdentifier -> PhoneNumberIdentifier(this.phoneNumber)
        is CommunicationIdentifier.UnknownIdentifier -> UnknownIdentifier(this.genericId)
    }
}

internal fun com.azure.android.communication.chat.models.ChatParticipant.into(): ChatParticipant {
    return ChatParticipantWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ChatThreadDeletedEvent.into(): ChatThreadDeletedEvent {
    return ChatThreadDeletedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ChatMessageEditedEvent.into(): ChatMessageEditedEvent {
    return ChatMessageEditedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ChatMessageReceivedEvent.into(): ChatMessageReceivedEvent {
    return ChatMessageReceivedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ParticipantsAddedEvent.into(): ParticipantsAddedEvent {
    return ParticipantsAddedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ParticipantsRemovedEvent.into(): ParticipantsRemovedEvent {
    return ParticipantsRemovedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ReadReceiptReceivedEvent.into(): ReadReceiptReceivedEvent {
    return ReadReceiptReceivedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.TypingIndicatorReceivedEvent.into(): TypingIndicatorReceivedEvent {
    return TypingIndicatorReceivedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ChatMessageDeletedEvent.into(): ChatMessageDeletedEvent {
    return ChatMessageDeletedEventWrapper(this)
}

internal fun com.azure.android.communication.chat.models.ChatMessage.into(): ChatMessage {
    return ChatMessageWrapper(this)
}
