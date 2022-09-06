// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.common.CommunicationIdentifier

internal class ParticipantIdentifierHelper {
    companion object {
        fun getRemoteParticipantId(identifier: CommunicationIdentifier): String {
            return when (identifier) {
                is PhoneNumberIdentifier -> {
                    identifier.phoneNumber
                }
                is MicrosoftTeamsUserIdentifier -> {
                    identifier.userId
                }
                is CommunicationUserIdentifier -> {
                    identifier.id
                }
                else -> {
                    (identifier as UnknownIdentifier).id
                }
            }
        }
    }
}
