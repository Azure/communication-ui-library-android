// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

// Should be a DataClass not an interface
internal interface ParticipantsAddedEvent {
    val addedOn: OffsetDateTime
    val participantsAdded: List<ChatParticipant>
}

internal data class ParticipantsRetrievedEvent(
    val participants: List<ChatParticipant>
)

internal class ParticipantsAddedEventWrapper(private val participantsAddedEvent: com.azure.android.communication.chat.models.ParticipantsAddedEvent) :
    ParticipantsAddedEvent {
    override val addedOn: OffsetDateTime = participantsAddedEvent.addedOn
    override val participantsAdded: List<ChatParticipant> =
        participantsAddedEvent.participantsAdded.map { it.into() }
}

internal class ParticipantsAddedPollingEventWrapper(
    private val on: OffsetDateTime,
    private val participants: List<ChatParticipant>
) :
    ParticipantsAddedEvent {
    override val addedOn: OffsetDateTime = on
    override val participantsAdded: List<ChatParticipant> = participants
}
