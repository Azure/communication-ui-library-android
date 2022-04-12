// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.persona.PersonaData
import com.azure.android.communication.ui.presentation.manager.PersonaManager
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.LocalUserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantListViewModel(private val personaManager: PersonaManager) {

    private lateinit var remoteParticipantListCellStateFlow: MutableStateFlow<List<ParticipantListCellModel>>
    private lateinit var localParticipantListCellStateFlow: MutableStateFlow<ParticipantListCellModel>
    private val displayParticipantListStateFlow = MutableStateFlow(false)

    fun getRemoteParticipantListCellStateFlow(): StateFlow<List<ParticipantListCellModel>> {
        return remoteParticipantListCellStateFlow
    }

    fun getLocalParticipantListCellStateFlow(): StateFlow<ParticipantListCellModel> {
        return localParticipantListCellStateFlow
    }

    fun getDisplayParticipantListStateFlow(): StateFlow<Boolean> {
        return displayParticipantListStateFlow
    }

    fun createLocalParticipantListCell(suffix: String) = ParticipantListCellModel(
        (localParticipantListCellStateFlow.value.displayName.trim() + " " + suffix).trim(),
        localParticipantListCellStateFlow.value.isMuted,
        personaManager.getLocalParticipantConfiguration()?.personaData
    )

    fun init(participantMap: Map<String, ParticipantInfoModel>, localUserState: LocalUserState) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                ParticipantListCellModel(
                    it.displayName.trim(),
                    it.isMuted,
                    personaManager.getRemoteParticipantsConfiguration().getPersonaData(it.userIdentifier)
                )
            }
        remoteParticipantListCellStateFlow = MutableStateFlow(remoteParticipantList)
        localParticipantListCellStateFlow =
            MutableStateFlow(
                ParticipantListCellModel(
                    localUserState.displayName ?: "",
                    localUserState.audioState.operation == AudioOperationalStatus.OFF,
                    personaManager.getLocalParticipantConfiguration()?.personaData
                )
            )
    }

    fun update(participantMap: Map<String, ParticipantInfoModel>, localUserState: LocalUserState) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                ParticipantListCellModel(
                    it.displayName.trim(),
                    it.isMuted,
                    personaManager.getRemoteParticipantPersonaData(it.userIdentifier)
                )
            }
        remoteParticipantListCellStateFlow.value = remoteParticipantList
        localParticipantListCellStateFlow.value =
            ParticipantListCellModel(
                localUserState.displayName ?: "",
                localUserState.audioState.operation == AudioOperationalStatus.OFF,
                personaManager.getLocalParticipantConfiguration()?.personaData
            )
    }

    fun displayParticipantList() {
        displayParticipantListStateFlow.value = true
    }

    fun closeParticipantList() {
        displayParticipantListStateFlow.value = false
    }
}

internal data class ParticipantListCellModel(
    val displayName: String,
    val isMuted: Boolean,
    val personaData: PersonaData?,
)
