package com.azure.android.communication.ui.participant.local

import com.azure.android.communication.ui.PersonaData
import kotlinx.coroutines.flow.MutableStateFlow

internal class LocalParticipantManagerImpl : LocalParticipantManager {

    val personaDataStateFlow: MutableStateFlow<PersonaData?> = MutableStateFlow(null)

    override fun setPersonaData(personaData: PersonaData?) {
        personaDataStateFlow.value = personaData
    }

    override fun getAvatar(): PersonaData {
        return personaDataStateFlow.value!!
    }
}
