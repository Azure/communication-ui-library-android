// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.RemoteParticipantViewData
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal class AvatarViewManager(
    coroutineContextProvider: CoroutineContextProvider,
    private val appStore: AppStore<ReduxState>,
    val callCompositeLocalOptions: CallCompositeLocalOptions?,
    configuration: RemoteParticipantsConfiguration,
) :
    RemoteParticipantsConfigurationHandler {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    init {
        configuration.setHandler(this)
    }

    private val remoteParticipantsPersonaCache = mutableMapOf<String, CallCompositeParticipantViewData>()
    private val remoteParticipantsPersonaSharedFlow =
        MutableSharedFlow<Map<String, CallCompositeParticipantViewData>>()

    override fun onSetParticipantViewData(data: RemoteParticipantViewData): CallCompositeSetParticipantViewDataResult {
        val id = data.identifier.id
        if (!appStore.getCurrentState().remoteParticipantState.participantMap.keys.contains(id)) {
            return CallCompositeSetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL
        }

        if (remoteParticipantsPersonaCache.contains(id)) {
            remoteParticipantsPersonaCache.remove(id)
        }
        remoteParticipantsPersonaCache[id] = data.participantViewData

        coroutineScope.launch {
            remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
        }

        return CallCompositeSetParticipantViewDataResult.SUCCESS
    }

    override fun onRemoveParticipantViewData(identifier: String) {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            remoteParticipantsPersonaCache.remove(identifier)
            coroutineScope.launch {
                remoteParticipantsPersonaSharedFlow.emit(remoteParticipantsPersonaCache)
            }
        }
    }

    fun getRemoteParticipantViewData(identifier: String): CallCompositeParticipantViewData? {
        if (remoteParticipantsPersonaCache.contains(identifier)) {
            return remoteParticipantsPersonaCache[identifier]
        }
        return null
    }

    fun getRemoteParticipantsPersonaSharedFlow(): SharedFlow<Map<String, CallCompositeParticipantViewData>> =
        remoteParticipantsPersonaSharedFlow
}
