// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData

internal data class RemoteParticipantPersonaData(
    val identifier: CommunicationIdentifier,
    val personaData: CommunicationUIPersonaData,
)

internal interface RemoteParticipantsConfigurationHandler {
    fun onSetRemoteParticipantPersonaData(data: RemoteParticipantPersonaData)
    fun getRemoteParticipantPersonaData(identifier: String): CommunicationUIPersonaData?
}

internal class RemoteParticipantsConfiguration {
    private var remoteParticipantsConfigurationHandler: RemoteParticipantsConfigurationHandler? =
        null

    fun getPersonaData(identifier: String) =
        remoteParticipantsConfigurationHandler?.getRemoteParticipantPersonaData(identifier)

    fun setRemoteParticipantsConfigurationHandler(handler: RemoteParticipantsConfigurationHandler) {
        remoteParticipantsConfigurationHandler = handler
    }

    fun setPersonaData(
        identifier: CommunicationIdentifier,
        personaData: CommunicationUIPersonaData,
    ) {
        remoteParticipantsConfigurationHandler?.onSetRemoteParticipantPersonaData(
            RemoteParticipantPersonaData(
                identifier,
                personaData
            )
        )
    }
}
