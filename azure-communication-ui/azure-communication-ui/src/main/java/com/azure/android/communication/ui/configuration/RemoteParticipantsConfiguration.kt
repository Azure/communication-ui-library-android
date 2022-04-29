// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.persona.PersonaData
import com.azure.android.communication.ui.persona.SetPersonaDataResult

internal data class RemoteParticipantPersonaData(
    val identifier: CommunicationIdentifier,
    val personaData: PersonaData,
)

internal interface RemoteParticipantsConfigurationHandler {
    fun onSetRemoteParticipantPersonaData(data: RemoteParticipantPersonaData) : SetPersonaDataResult
    fun getRemoteParticipantPersonaData(identifier: String): PersonaData?
}

internal class RemoteParticipantsConfiguration {
    private lateinit var remoteParticipantsConfigurationHandler: RemoteParticipantsConfigurationHandler

    fun setRemoteParticipantsConfigurationHandler(handler: RemoteParticipantsConfigurationHandler) {
        remoteParticipantsConfigurationHandler = handler
    }

    fun setPersonaData(
        identifier: CommunicationIdentifier,
        personaData: PersonaData,
    ): SetPersonaDataResult {
        return remoteParticipantsConfigurationHandler.onSetRemoteParticipantPersonaData(
            RemoteParticipantPersonaData(
                identifier,
                personaData
            )
        )
    }
}
