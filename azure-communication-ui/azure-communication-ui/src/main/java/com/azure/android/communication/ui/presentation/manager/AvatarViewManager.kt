// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.configuration.CommunicationUILocalDataOptions
import com.azure.android.communication.ui.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.ParticipantIdentifierHelper
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal class AvatarViewManager(
    coroutineContextProvider: CoroutineContextProvider,
    val communicationUILocalDataOptions: CommunicationUILocalDataOptions?,
    remoteParticipantsConfiguration: RemoteParticipantsConfiguration,
) :
    RemoteParticipantsConfigurationHandler {

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    init {
        remoteParticipantsConfiguration.setRemoteParticipantsConfigurationHandler(this)
    }

    private val remoteParticipantsPersonaCache = mutableMapOf<String, CommunicationUIPersonaData>()
    private val remoteParticipantsPersonaSharedFlow =
        MutableSharedFlow<Map<String, CommunicationUIPersonaData>>()

    fun getRemoteParticipantsPersonaSharedFlow(): SharedFlow<Map<String, CommunicationUIPersonaData>> =
        remoteParticipantsPersonaSharedFlow

    override fun onSetRemoteParticipantPersonaData(data: RemoteParticipantPersonaData) {
        val id = ParticipantIdentifierHelper.getRemoteParticipantId(data.identifier)
        if (remoteParticipantsPersonaCache.contains(id)) {
            remoteParticipantsPersonaCache.remove(id)
        }
        remoteParticipantsPersonaCache[id] = data.personaData

        coroutineScope.launch {
            remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
        }
    }

    override fun getRemoteParticipantPersonaData(identifier: String): CommunicationUIPersonaData? {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            return remoteParticipantsPersonaCache[identifier]
        }
        return null
    }
}
