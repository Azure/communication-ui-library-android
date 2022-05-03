// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.LocalUserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantListViewModel {
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
        ""
    )

    fun init(participantMap: Map<String, ParticipantInfoModel>, localUserState: LocalUserState) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                getRemoteParticipantListCellModel(it)
            }
        remoteParticipantListCellStateFlow = MutableStateFlow(remoteParticipantList)

        localParticipantListCellStateFlow =
            MutableStateFlow(getLocalParticipantListCellModel(localUserState))
    }

    fun update(participantMap: Map<String, ParticipantInfoModel>, localUserState: LocalUserState) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                getRemoteParticipantListCellModel(it)
            }
        remoteParticipantListCellStateFlow.value = remoteParticipantList

        localParticipantListCellStateFlow.value = getLocalParticipantListCellModel(localUserState)
    }

    fun displayParticipantList() {
        displayParticipantListStateFlow.value = true
    }

    fun closeParticipantList() {
        displayParticipantListStateFlow.value = false
    }

    private fun getLocalParticipantListCellModel(localUserState: LocalUserState): ParticipantListCellModel {
        val localUserDisplayName = localUserState.displayName
        return ParticipantListCellModel(
            localUserDisplayName ?: "",
            localUserState.audioState.operation == AudioOperationalStatus.OFF,
            ""
        )
    }

    private fun getRemoteParticipantListCellModel(it: ParticipantInfoModel): ParticipantListCellModel {
        return ParticipantListCellModel(
            it.displayName.trim(),
            it.isMuted,
            it.userIdentifier
        )
    }
}

internal data class ParticipantListCellModel(
    val displayName: String,
    val isMuted: Boolean,
    val userIdentifier: String,
)
