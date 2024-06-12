// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeInternalParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.calling.CallingCommunicationErrors

internal fun com.azure.android.communication.calling.RemoteParticipant.into(): RemoteParticipant {
    return RemoteParticipantWrapper(this)
}

internal fun com.azure.android.communication.common.CommunicationIdentifier.into(): CommunicationIdentifier {
    return when (this) {
        is CommunicationUserIdentifier -> CommunicationIdentifier.CommunicationUserIdentifier(this.id)
        is MicrosoftTeamsUserIdentifier -> CommunicationIdentifier.MicrosoftTeamsUserIdentifier(this.userId, this.isAnonymous)
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
        is CommunicationIdentifier.MicrosoftTeamsUserIdentifier -> MicrosoftTeamsUserIdentifier(this.userId, this.isAnonymous)
        is CommunicationIdentifier.PhoneNumberIdentifier -> PhoneNumberIdentifier(this.phoneNumber)
        is CommunicationIdentifier.UnknownIdentifier -> UnknownIdentifier(this.genericId)
    }
}

internal fun com.azure.android.communication.calling.RemoteVideoStream.into(): RemoteVideoStream {
    return RemoteVideoStreamWrapper(this)
}

internal fun com.azure.android.communication.calling.VideoDeviceInfo.into(): VideoDeviceInfo {
    return VideoDeviceInfo(native = this, this.id, this.name, this.cameraFacing, this.deviceType)
}

internal fun com.azure.android.communication.calling.VideoStreamRendererView.into(): VideoStreamRendererView {
    return VideoStreamRendererViewWrapper(this)
}

internal fun ParticipantState.into(): ParticipantStatus {
    return when (this) {
        ParticipantState.IDLE -> ParticipantStatus.IDLE
        ParticipantState.EARLY_MEDIA -> ParticipantStatus.EARLY_MEDIA
        ParticipantState.CONNECTING -> ParticipantStatus.CONNECTING
        ParticipantState.HOLD -> ParticipantStatus.HOLD
        ParticipantState.DISCONNECTED -> ParticipantStatus.DISCONNECTED
        ParticipantState.IN_LOBBY -> ParticipantStatus.IN_LOBBY
        ParticipantState.RINGING -> ParticipantStatus.RINGING
        ParticipantState.CONNECTED -> ParticipantStatus.CONNECTED
    }
}

internal fun com.azure.android.communication.calling.StreamSize.into(): StreamSize {
    return StreamSize(this.width, this.height)
}

internal fun com.azure.android.communication.calling.DominantSpeakersInfo.into(): DominantSpeakersInfo {
    return DominantSpeakersInfoWrapper(this)
}

internal fun com.azure.android.communication.calling.CallParticipantRole.into(): CallCompositeInternalParticipantRole? {
    return when (this) {
        com.azure.android.communication.calling.CallParticipantRole.ATTENDEE -> CallCompositeInternalParticipantRole.ATTENDEE
        com.azure.android.communication.calling.CallParticipantRole.CONSUMER -> CallCompositeInternalParticipantRole.CONSUMER
        com.azure.android.communication.calling.CallParticipantRole.PRESENTER -> CallCompositeInternalParticipantRole.PRESENTER
        com.azure.android.communication.calling.CallParticipantRole.ORGANIZER -> CallCompositeInternalParticipantRole.ORGANIZER
        com.azure.android.communication.calling.CallParticipantRole.CO_ORGANIZER -> CallCompositeInternalParticipantRole.COORGANIZER
        com.azure.android.communication.calling.CallParticipantRole.UNINITIALIZED -> CallCompositeInternalParticipantRole.UNINITIALIZED
        else -> { null }
    }
}

internal fun getLobbyErrorCode(error: CallingCommunicationException) =
    when (error.errorCode) {
        CallingCommunicationErrors.LOBBY_DISABLED_BY_CONFIGURATIONS -> {
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
        }

        CallingCommunicationErrors.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED -> {
            CallCompositeLobbyErrorCode.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED
        }

        CallingCommunicationErrors.LOBBY_MEETING_ROLE_NOT_ALLOWED -> {
            CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED
        }

        CallingCommunicationErrors.REMOVE_PARTICIPANT_OPERATION_FAILURE -> {
            CallCompositeLobbyErrorCode.REMOVE_PARTICIPANT_OPERATION_FAILURE
        }
        else -> {
            CallCompositeLobbyErrorCode.UNKNOWN_ERROR
        }
    }
