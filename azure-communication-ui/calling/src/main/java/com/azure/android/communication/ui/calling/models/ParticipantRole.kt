// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal enum class ParticipantRole {
    UNINITIALIZED,
    ATTENDEE,
    CONSUMER,
    PRESENTER,
    ORGANIZER,
}

internal fun CallCompositeParticipantRole.into(): ParticipantRole {
    return when (this) {
        CallCompositeParticipantRole.PRESENTER -> ParticipantRole.PRESENTER
        CallCompositeParticipantRole.ATTENDEE -> ParticipantRole.ATTENDEE
        CallCompositeParticipantRole.CONSUMER -> ParticipantRole.CONSUMER
        else -> ParticipantRole.UNINITIALIZED
    }
}
