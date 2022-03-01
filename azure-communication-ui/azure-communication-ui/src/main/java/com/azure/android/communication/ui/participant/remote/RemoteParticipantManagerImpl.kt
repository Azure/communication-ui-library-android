package com.azure.android.communication.ui.participant.remote

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.AvatarPersonaData
import kotlinx.coroutines.flow.MutableStateFlow

internal class RemoteParticipantManagerImpl: RemoteParticipantManager {

    val avatarPersonaDataStateFlow: MutableStateFlow<AvatarPersonaData?> = MutableStateFlow(null)


    override fun setRemoteParticipantAvatar(
        communicationIdentifier: CommunicationIdentifier,
        avatarPersonaData: AvatarPersonaData,
    ) {
        avatarPersonaDataStateFlow.value = avatarPersonaData
    }

    override fun getRemoteParticipantAvatar(communicationIdentifier: CommunicationIdentifier): AvatarPersonaData {
        TODO("Not yet implemented")
    }


}