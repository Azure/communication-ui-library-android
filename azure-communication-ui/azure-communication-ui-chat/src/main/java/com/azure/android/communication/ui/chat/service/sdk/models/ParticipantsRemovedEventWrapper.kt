// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ParticipantsRemovedEvent {
    val removedOn: OffsetDateTime
    val participantsRemoved: List<ChatParticipant>
}

internal class ParticipantsRemovedEventWrapper(private val participantsRemovedEvent: com.azure.android.communication.chat.models.ParticipantsRemovedEvent) :
    ParticipantsRemovedEvent {
    override val removedOn: OffsetDateTime = participantsRemovedEvent.removedOn
    override val participantsRemoved: List<ChatParticipant> =
        participantsRemovedEvent.participantsRemoved.map { it.into() }
}

internal class ParticipantsRemovedEventWrapperPolling(
    private val on: OffsetDateTime,
    private val participants: List<ChatParticipant>
) :
    ParticipantsRemovedEvent {
    override val removedOn: OffsetDateTime = on
    override val participantsRemoved: List<ChatParticipant> = participants
}
