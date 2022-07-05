// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier

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
