// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.calling.models.ParticipantStatus

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
