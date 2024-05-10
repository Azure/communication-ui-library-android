// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.calling.CapabilityResolutionReason
import com.azure.android.communication.calling.CapabilityResolutionReason as SdkCapabilityResolutionReason
import com.azure.android.communication.calling.CapabilitiesChangedEvent as SdkCapabilitiesChangedEvent
import com.azure.android.communication.calling.CapabilitiesChangedReason as SdkCapabilitiesChangedReason
import com.azure.android.communication.calling.ParticipantCapability as SdkParticipantCapability
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilitiesChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilitiesChangedReason
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilityResolutionReason
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantCapability
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.createCallCompositeCapabilitiesChangedEvent
import com.azure.android.communication.ui.calling.models.createCallCompositeParticipantCapability
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

internal fun SdkParticipantCapability.into(): CallCompositeParticipantCapability? {

    val isCallingUiSupportedCapability = this.type.into() != null

    if (isCallingUiSupportedCapability) {
        return createCallCompositeParticipantCapability(
            this.type.into()!!,
            this.isAllowed,
            this.reason.into(),
        )
    }
    return null
}

internal fun SdkCapabilitiesChangedEvent.into(): CallCompositeCapabilitiesChangedEvent {
    return createCallCompositeCapabilitiesChangedEvent(
        this.changedCapabilities.mapNotNull { it.into() },
        this.reason.into()
    )
}

internal fun SdkCapabilitiesChangedReason.into(): CallCompositeCapabilitiesChangedReason {
    return when (this) {
        SdkCapabilitiesChangedReason.ROLE_CHANGED ->
            CallCompositeCapabilitiesChangedReason.ROLE_CHANGED
        SdkCapabilitiesChangedReason.USER_POLICY_CHANGED ->
            CallCompositeCapabilitiesChangedReason.USER_POLICY_CHANGED
        SdkCapabilitiesChangedReason.MEETING_DETAILS_CHANGED ->
            CallCompositeCapabilitiesChangedReason.MEETING_DETAILS_CHANGED
    }
}

internal fun SdkParticipantCapabilityType.into(): CallCompositeParticipantCapabilityType? {
    return when (this) {
        SdkParticipantCapabilityType.TURN_VIDEO_ON -> CallCompositeParticipantCapabilityType.TURN_VIDEO_ON
        SdkParticipantCapabilityType.UNMUTE_MICROPHONE -> CallCompositeParticipantCapabilityType.UNMUTE_MICROPHONE
        SdkParticipantCapabilityType.REMOVE_PARTICIPANT -> CallCompositeParticipantCapabilityType.REMOVE_PARTICIPANT
        SdkParticipantCapabilityType.MANAGE_LOBBY -> CallCompositeParticipantCapabilityType.MANAGE_LOBBY
        else -> null
    }
}

internal fun SdkCapabilityResolutionReason.into(): CallCompositeCapabilityResolutionReason {
    return when (this) {
        SdkCapabilityResolutionReason.CAPABLE -> CallCompositeCapabilityResolutionReason.CAPABLE
        CapabilityResolutionReason.CALL_TYPE_RESTRICTED -> CallCompositeCapabilityResolutionReason.CALL_TYPE_RESTRICTED
        CapabilityResolutionReason.USER_POLICY_RESTRICTED -> CallCompositeCapabilityResolutionReason.USER_POLICY_RESTRICTED
        CapabilityResolutionReason.ROLE_RESTRICTED -> CallCompositeCapabilityResolutionReason.ROLE_RESTRICTED
        CapabilityResolutionReason.MEETING_RESTRICTED -> CallCompositeCapabilityResolutionReason.MEETING_RESTRICTED
        CapabilityResolutionReason.FEATURE_NOT_SUPPORTED -> CallCompositeCapabilityResolutionReason.FEATURE_NOT_SUPPORTED
        CapabilityResolutionReason.NOT_INITIALIZED -> CallCompositeCapabilityResolutionReason.NOT_INITIALIZED
        CapabilityResolutionReason.NOT_CAPABLE -> CallCompositeCapabilityResolutionReason.NOT_CAPABLE
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
