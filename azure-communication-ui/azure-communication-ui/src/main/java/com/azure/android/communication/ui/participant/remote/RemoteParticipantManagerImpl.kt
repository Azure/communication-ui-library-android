package com.azure.android.communication.ui.participant.remote

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.AvatarData
import kotlinx.coroutines.flow.MutableStateFlow

internal class RemoteParticipantManagerImpl : RemoteParticipantManager {

    val avatarDataStateFlow: MutableStateFlow<AvatarData?> = MutableStateFlow(null)

    override fun setAvatar(
        communicationIdentifier: CommunicationIdentifier,
        avatarData: AvatarData,
    ) {
        avatarDataStateFlow.value = avatarData
    }

    override fun getAvatar(communicationIdentifier: CommunicationIdentifier): AvatarData {
        TODO("Not yet implemented")
    }
}
