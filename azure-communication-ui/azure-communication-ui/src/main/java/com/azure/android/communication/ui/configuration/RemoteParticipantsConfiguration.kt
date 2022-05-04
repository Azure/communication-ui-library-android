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
    fun onSetPersonaData(data: RemoteParticipantPersonaData): SetPersonaDataResult
    fun onRemovePersonaData(identifier: String)
}

internal class RemoteParticipantsConfiguration {
    private var handler: RemoteParticipantsConfigurationHandler? = null

    fun setHandler(handler: RemoteParticipantsConfigurationHandler) {
        this.handler = handler
    }

    fun setPersonaData(
        identifier: CommunicationIdentifier,
        personaData: PersonaData,
    ): SetPersonaDataResult {
        if (handler != null) {
            return handler!!.onSetPersonaData(
                RemoteParticipantPersonaData(
                    identifier,
                    personaData
                )
            )
        }
        return SetPersonaDataResult.PARTICIPANT_NOT_IN_CALL
    }

    fun removePersonaData(identifier: String) {
        handler?.onRemovePersonaData(identifier)
    }
}
