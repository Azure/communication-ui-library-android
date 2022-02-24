package com.azure.android.communication.ui.participant.local

import com.azure.android.communication.ui.AvatarPersonaData
import kotlinx.coroutines.flow.MutableStateFlow

internal class LocalParticipantManagerImpl: LocalParticipantManager {

     val avatarPersonaDataStateFlow: MutableStateFlow<AvatarPersonaData?> = MutableStateFlow(null)


    override fun setLocalParticipantAvatar(avatarPersonaData: AvatarPersonaData?) {
        avatarPersonaDataStateFlow.value = avatarPersonaData
    }
}