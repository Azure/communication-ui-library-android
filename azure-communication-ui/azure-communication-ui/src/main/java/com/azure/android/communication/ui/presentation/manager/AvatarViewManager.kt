// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.configuration.LocalDataOptions
import com.azure.android.communication.ui.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.persona.PersonaData
import com.azure.android.communication.ui.persona.SetPersonaDataResult
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.ParticipantIdentifierHelper
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal class AvatarViewManager(
    coroutineContextProvider: CoroutineContextProvider,
    private val appStore: AppStore<ReduxState>,
    val localDataOptions: LocalDataOptions?,
    remoteParticipantsConfiguration: RemoteParticipantsConfiguration,
) :
    RemoteParticipantsConfigurationHandler {

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    init {
        remoteParticipantsConfiguration.setRemoteParticipantsConfigurationHandler(this)
    }

    private val remoteParticipantsPersonaCache = mutableMapOf<String, PersonaData>()
    private val remoteParticipantsPersonaSharedFlow =
        MutableSharedFlow<Map<String, PersonaData>>()

    fun getRemoteParticipantsPersonaSharedFlow(): SharedFlow<Map<String, PersonaData>> =
        remoteParticipantsPersonaSharedFlow

    override fun onSetRemoteParticipantPersonaData(data: RemoteParticipantPersonaData) : SetPersonaDataResult {
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

    override fun getRemoteParticipantPersonaData(identifier: String): PersonaData? {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            return remoteParticipantsPersonaCache[identifier]
        }
        return null
    }
}
