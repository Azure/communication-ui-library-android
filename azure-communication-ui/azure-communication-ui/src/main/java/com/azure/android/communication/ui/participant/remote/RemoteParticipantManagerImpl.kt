package com.azure.android.communication.ui.participant.remote

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.PersonaData
import kotlinx.coroutines.flow.MutableStateFlow

internal class RemoteParticipantManagerImpl : RemoteParticipantManager {

    val personaDataStateFlow: MutableStateFlow<PersonaData?> = MutableStateFlow(null)

    override fun setPersonaData(
        communicationIdentifier: CommunicationIdentifier,
        personaData: PersonaData,
    ) {
        personaDataStateFlow.value = personaData
    }

    override fun getAvatar(communicationIdentifier: CommunicationIdentifier): PersonaData {
        TODO("Not yet implemented")
    }
}
