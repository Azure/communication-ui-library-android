// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

internal sealed class CommunicationIdentifier(val id: String) {
    data class CommunicationUserIdentifier(val userId: String) : CommunicationIdentifier(userId)
    data class MicrosoftTeamsUserIdentifier(val userId: String, val isAnonymous: Boolean) :
        CommunicationIdentifier(userId)

    data class PhoneNumberIdentifier(val phoneNumber: String) : CommunicationIdentifier(phoneNumber)
    data class UnknownIdentifier(val genericId: String) : CommunicationIdentifier(genericId)
}
