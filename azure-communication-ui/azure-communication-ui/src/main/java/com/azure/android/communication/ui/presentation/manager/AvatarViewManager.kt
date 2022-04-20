// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.configuration.CommunicationUILocalDataOptions
import com.azure.android.communication.ui.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.ParticipantAction
import com.azure.android.communication.ui.redux.state.ReduxState

internal class AvatarViewManager(
    private val appStore: AppStore<ReduxState>,
    val communicationUILocalDataOptions: CommunicationUILocalDataOptions?,
    val remoteParticipantsConfiguration: RemoteParticipantsConfiguration,
) :
    RemoteParticipantsConfigurationHandler {

    init {
        remoteParticipantsConfiguration.setRemoteParticipantsConfigurationHandler(this)
    }

    private val remoteParticipantsPersonaCache = mutableMapOf<String, CommunicationUIPersonaData>()

    override fun onSetRemoteParticipantPersonaData(data: RemoteParticipantPersonaData) {
        val id = getRemoteParticipantId(data.identifier)
        if (remoteParticipantsPersonaCache.contains(id)) {
            remoteParticipantsPersonaCache.remove(id)
        }
        remoteParticipantsPersonaCache[id] = data.personaData
        appStore.dispatch(ParticipantAction.PersonaUpdated(id))
    }

    override fun getRemoteParticipantPersonaData(identifier: String): CommunicationUIPersonaData? {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            return remoteParticipantsPersonaCache[identifier]
        }
        return null
    }

    private fun getRemoteParticipantId(identifier: CommunicationIdentifier): String {
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
