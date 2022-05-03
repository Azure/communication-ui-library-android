// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.LocalDataOptions
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.calling.persona.PersonaData
import com.azure.android.communication.ui.calling.persona.SetPersonaDataResult
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.ParticipantIdentifierHelper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal class AvatarViewManager(
    coroutineContextProvider: CoroutineContextProvider,
    private val appStore: AppStore<ReduxState>,
    val localDataOptions: LocalDataOptions?,
    configuration: RemoteParticipantsConfiguration,
) :
    RemoteParticipantsConfigurationHandler {

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    init {
        configuration.setHandler(this)
    }

    private val remoteParticipantsPersonaCache = mutableMapOf<String, PersonaData>()
    private val remoteParticipantsPersonaSharedFlow =
        MutableSharedFlow<Map<String, PersonaData>>()

    override fun onSetPersonaData(data: RemoteParticipantPersonaData): SetPersonaDataResult {
        val id = ParticipantIdentifierHelper.getRemoteParticipantId(data.identifier)
        if (!appStore.getCurrentState().remoteParticipantState.participantMap.keys.contains(id)) {
            return SetPersonaDataResult.PARTICIPANT_NOT_IN_CALL
        }

        if (remoteParticipantsPersonaCache.contains(id)) {
            remoteParticipantsPersonaCache.remove(id)
        }
        remoteParticipantsPersonaCache[id] = data.personaData

        coroutineScope.launch {
            remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
        }

        return SetPersonaDataResult.SUCCESS
    }

    override fun onRemovePersonaData(identifier: String) {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            remoteParticipantsPersonaCache.remove(identifier)
            coroutineScope.launch {
                remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
            }
        }
    }

    fun getRemoteParticipantPersonaData(identifier: String): PersonaData? {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            return remoteParticipantsPersonaCache[identifier]
        }
        return null
    }

    fun getRemoteParticipantsPersonaSharedFlow(): SharedFlow<Map<String, PersonaData>> =
        remoteParticipantsPersonaSharedFlow
}
