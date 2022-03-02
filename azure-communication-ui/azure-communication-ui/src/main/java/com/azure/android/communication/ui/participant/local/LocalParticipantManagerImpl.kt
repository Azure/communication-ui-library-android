package com.azure.android.communication.ui.participant.local

import com.azure.android.communication.ui.AvatarData
import kotlinx.coroutines.flow.MutableStateFlow

internal class LocalParticipantManagerImpl : LocalParticipantManager {

    val avatarDataStateFlow: MutableStateFlow<AvatarData?> = MutableStateFlow(null)

    override fun setAvatar(avatarData: AvatarData?) {
        avatarDataStateFlow.value = avatarData
    }

    override fun getAvatar(): AvatarData {
        return avatarDataStateFlow.value!!
    }
}
