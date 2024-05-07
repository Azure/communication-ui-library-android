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
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.calling.ParticipantCapabilityType as SdkParticipantCapabilityType

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

internal fun com.azure.android.communication.calling.CallParticipantRole.into(): ParticipantRole? {
    return when (this) {
        com.azure.android.communication.calling.CallParticipantRole.ATTENDEE -> ParticipantRole.ATTENDEE
        com.azure.android.communication.calling.CallParticipantRole.CONSUMER -> ParticipantRole.CONSUMER
        com.azure.android.communication.calling.CallParticipantRole.PRESENTER -> ParticipantRole.PRESENTER
        com.azure.android.communication.calling.CallParticipantRole.ORGANIZER -> ParticipantRole.ORGANIZER
        com.azure.android.communication.calling.CallParticipantRole.UNINITIALIZED -> ParticipantRole.UNINITIALIZED
        else -> { null }
    }
}

internal fun List<com.azure.android.communication.calling.ParticipantCapability>.into(): List<ParticipantCapabilityType> {
    return this
        .filter { it.isAllowed }
        .filter { it.type != null }
        .map {
            when (it.type) {
                SdkParticipantCapabilityType.ADD_COMMUNICATION_USER -> ParticipantCapabilityType.ADD_COMMUNICATION_USER
                SdkParticipantCapabilityType.TURN_VIDEO_ON -> ParticipantCapabilityType.TURN_VIDEO_ON
                SdkParticipantCapabilityType.UNMUTE_MICROPHONE -> ParticipantCapabilityType.UNMUTE_MICROPHONE
                SdkParticipantCapabilityType.SHARE_SCREEN -> ParticipantCapabilityType.SHARE_SCREEN
                SdkParticipantCapabilityType.REMOVE_PARTICIPANT -> ParticipantCapabilityType.REMOVE_PARTICIPANT
                SdkParticipantCapabilityType.HANG_UP_FOR_EVERYONE -> ParticipantCapabilityType.HANG_UP_FOR_EVERY_ONE
                SdkParticipantCapabilityType.ADD_TEAMS_USER -> ParticipantCapabilityType.ADD_TEAMS_USER
                SdkParticipantCapabilityType.ADD_PHONE_NUMBER -> ParticipantCapabilityType.ADD_PHONE_NUMBER
                SdkParticipantCapabilityType.MANAGE_LOBBY -> ParticipantCapabilityType.MANAGE_LOBBY
                SdkParticipantCapabilityType.SPOTLIGHT_PARTICIPANT -> ParticipantCapabilityType.SPOTLIGHT_PARTICIPANT
                SdkParticipantCapabilityType.REMOVE_PARTICIPANT_SPOTLIGHT -> ParticipantCapabilityType.REMOVE_PARTICIPANT_SPOTLIGHT
                SdkParticipantCapabilityType.BLUR_BACKGROUND -> ParticipantCapabilityType.BLUR_BACKGROUND
                SdkParticipantCapabilityType.CUSTOM_BACKGROUND -> ParticipantCapabilityType.CUSTOM_BACKGROUND
                SdkParticipantCapabilityType.START_LIVE_CAPTIONS -> ParticipantCapabilityType.START_LIVE_CAPTIONS
                SdkParticipantCapabilityType.RAISE_HAND -> ParticipantCapabilityType.RAISE_HAND
            }
        }
}

internal fun getLobbyErrorCode(error: CallingCommunicationException) = CallCompositeLobbyErrorCode.UNKNOWN_ERROR
//    when (error.errorCode) {
//        CallingCommunicationErrors.LOBBY_DISABLED_BY_CONFIGURATIONS -> {
//            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
//        }
//
//        CallingCommunicationErrors.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED -> {
//            CallCompositeLobbyErrorCode.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED
//        }
//
//        CallingCommunicationErrors.LOBBY_MEETING_ROLE_NOT_ALLOWED -> {
//            CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED
//        }
//
//        CallingCommunicationErrors.REMOVE_PARTICIPANT_OPERATION_FAILURE -> {
//            CallCompositeLobbyErrorCode.REMOVE_PARTICIPANT_OPERATION_FAILURE
//        }
//        else -> {
//            CallCompositeLobbyErrorCode.UNKNOWN_ERROR
//        }
//    }
