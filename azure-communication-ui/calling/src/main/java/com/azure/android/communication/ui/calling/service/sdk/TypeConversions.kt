// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallingCommunicationErrors as SdkCallingCommunicationErrors
import com.azure.android.communication.calling.CallingCommunicationException as SdkCallingCommunicationException
import com.azure.android.communication.calling.CapabilityResolutionReason as SdkCapabilityResolutionReason
import com.azure.android.communication.calling.CapabilitiesChangedEvent as SdkCapabilitiesChangedEvent
import com.azure.android.communication.calling.CapabilitiesChangedReason as SdkCapabilitiesChangedReason
import com.azure.android.communication.calling.ParticipantCapability as SdkParticipantCapability
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CapabilitiesChangedEvent
import com.azure.android.communication.ui.calling.models.CapabilitiesChangedReason
import com.azure.android.communication.ui.calling.models.CapabilityResolutionReason
import com.azure.android.communication.ui.calling.models.ParticipantCapability
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole
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

internal fun SdkParticipantCapability.into(): ParticipantCapability? {

    val isCallingUiSupportedCapability = this.type.into() != null

    if (isCallingUiSupportedCapability) {
        return ParticipantCapability(
            this.type.into()!!,
            this.isAllowed,
            this.reason.into(),
        )
    }
    return null
}

internal fun SdkCapabilitiesChangedEvent.into(): CapabilitiesChangedEvent {
    return CapabilitiesChangedEvent(
        this.changedCapabilities.mapNotNull { it.into() },
        this.reason.into()
    )
}

internal fun SdkCapabilitiesChangedReason.into(): CapabilitiesChangedReason {
    return when (this) {
        SdkCapabilitiesChangedReason.ROLE_CHANGED ->
            CapabilitiesChangedReason.ROLE_CHANGED
        SdkCapabilitiesChangedReason.USER_POLICY_CHANGED ->
            CapabilitiesChangedReason.USER_POLICY_CHANGED
        SdkCapabilitiesChangedReason.MEETING_DETAILS_CHANGED ->
            CapabilitiesChangedReason.MEETING_DETAILS_CHANGED
    }
}

internal fun SdkParticipantCapabilityType.into(): ParticipantCapabilityType? {
    return when (this) {
        SdkParticipantCapabilityType.TURN_VIDEO_ON -> ParticipantCapabilityType.TURN_VIDEO_ON
        SdkParticipantCapabilityType.UNMUTE_MICROPHONE -> ParticipantCapabilityType.UNMUTE_MICROPHONE
        SdkParticipantCapabilityType.REMOVE_PARTICIPANT -> ParticipantCapabilityType.REMOVE_PARTICIPANT
        SdkParticipantCapabilityType.MANAGE_LOBBY -> ParticipantCapabilityType.MANAGE_LOBBY
        else -> null
    }
}

internal fun SdkCapabilityResolutionReason.into(): CapabilityResolutionReason {
    return when (this) {
        SdkCapabilityResolutionReason.CAPABLE -> CapabilityResolutionReason.CAPABLE
        SdkCapabilityResolutionReason.CALL_TYPE_RESTRICTED -> CapabilityResolutionReason.CALL_TYPE_RESTRICTED
        SdkCapabilityResolutionReason.USER_POLICY_RESTRICTED -> CapabilityResolutionReason.USER_POLICY_RESTRICTED
        SdkCapabilityResolutionReason.ROLE_RESTRICTED -> CapabilityResolutionReason.ROLE_RESTRICTED
        SdkCapabilityResolutionReason.MEETING_RESTRICTED -> CapabilityResolutionReason.MEETING_RESTRICTED
        SdkCapabilityResolutionReason.FEATURE_NOT_SUPPORTED -> CapabilityResolutionReason.FEATURE_NOT_SUPPORTED
        SdkCapabilityResolutionReason.NOT_INITIALIZED -> CapabilityResolutionReason.NOT_INITIALIZED
        SdkCapabilityResolutionReason.NOT_CAPABLE -> CapabilityResolutionReason.NOT_CAPABLE
    }
}

internal fun getLobbyErrorCode(error: SdkCallingCommunicationException) =
    when (error.errorCode) {
        SdkCallingCommunicationErrors.LOBBY_DISABLED_BY_CONFIGURATIONS -> {
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS
        }

        SdkCallingCommunicationErrors.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED -> {
            CallCompositeLobbyErrorCode.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED
        }

        SdkCallingCommunicationErrors.LOBBY_MEETING_ROLE_NOT_ALLOWED -> {
            CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED
        }

        SdkCallingCommunicationErrors.REMOVE_PARTICIPANT_OPERATION_FAILURE -> {
            CallCompositeLobbyErrorCode.REMOVE_PARTICIPANT_OPERATION_FAILURE
        }
        else -> {
            CallCompositeLobbyErrorCode.UNKNOWN_ERROR
        }
    }
