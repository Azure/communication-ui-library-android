// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.LocalSettings
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantViewData
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.calling.models.ParticipantViewData
import com.azure.android.communication.ui.calling.models.SetParticipantViewDataResult
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
    val localSettings: LocalSettings?,
    configuration: RemoteParticipantsConfiguration,
) :
    RemoteParticipantsConfigurationHandler {

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    init {
        configuration.setHandler(this)
    }

    private val remoteParticipantsPersonaCache = mutableMapOf<String, ParticipantViewData>()
    private val remoteParticipantsPersonaSharedFlow =
        MutableSharedFlow<Map<String, ParticipantViewData>>()

    override fun onSetParticipantViewData(data: RemoteParticipantViewData): SetParticipantViewDataResult {
        val id = ParticipantIdentifierHelper.getRemoteParticipantId(data.identifier)
        if (!appStore.getCurrentState().remoteParticipantState.participantMap.keys.contains(id)) {
            return SetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL
        }

        if (remoteParticipantsPersonaCache.contains(id)) {
            remoteParticipantsPersonaCache.remove(id)
        }
        remoteParticipantsPersonaCache[id] = data.participantViewData

        coroutineScope.launch {
            remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
        }

        return SetParticipantViewDataResult.SUCCESS
    }

    override fun onRemoveParticipantViewData(identifier: String) {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            remoteParticipantsPersonaCache.remove(identifier)
            coroutineScope.launch {
                remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
            }
        }
    }

    fun getRemoteParticipantViewData(identifier: String): ParticipantViewData? {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            return remoteParticipantsPersonaCache[identifier]
        }
        return null
    }

    fun getRemoteParticipantsPersonaSharedFlow(): SharedFlow<Map<String, ParticipantViewData>> =
        remoteParticipantsPersonaSharedFlow
}
