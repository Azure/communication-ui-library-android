// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantListViewModel(private val dispatch: (Action) -> Unit) {
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
        "",
        false
    )

    fun init(
        participantMap: Map<String, ParticipantInfoModel>,
        localUserState: LocalUserState,
        canShowLobby: Boolean,
    ) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                getRemoteParticipantListCellModel(it)
            }.filter { participantListRemoteParticipantVisibility(it, canShowLobby) }
        remoteParticipantListCellStateFlow = MutableStateFlow(remoteParticipantList)

        localParticipantListCellStateFlow =
            MutableStateFlow(getLocalParticipantListCellModel(localUserState))
    }

    fun update(
        participantMap: Map<String, ParticipantInfoModel>,
        localUserState: LocalUserState,
        visibilityState: VisibilityState,
        canShowLobby: Boolean
    ) {
        val remoteParticipantList: MutableList<ParticipantListCellModel> =
            participantMap.values.map {
                getRemoteParticipantListCellModel(it)
            }.filter { participantListRemoteParticipantVisibility(it, canShowLobby) }.toMutableList()
        remoteParticipantListCellStateFlow.value = remoteParticipantList

        localParticipantListCellStateFlow.value = getLocalParticipantListCellModel(localUserState)

        if (visibilityState.status != VisibilityStatus.VISIBLE)
            closeParticipantList()
    }

    private fun participantListRemoteParticipantVisibility(
        it: ParticipantListCellModel,
        canShowLobby: Boolean,
    ) = (
        it.status != ParticipantStatus.DISCONNECTED &&
            if (it.status == ParticipantStatus.IN_LOBBY) canShowLobby else true
        )

    fun displayParticipantList() {
        displayParticipantListStateFlow.value = true
    }

    fun closeParticipantList() {
        displayParticipantListStateFlow.value = false
    }

    fun admitParticipant(userIdentifier: String) {
        dispatch(ParticipantAction.Admit(userIdentifier))
    }

    fun declineParticipant(userIdentifier: String) {
        dispatch(ParticipantAction.Decline(userIdentifier))
    }

    fun admitAllLobbyParticipants() {
        dispatch(ParticipantAction.AdmitAll())
    }

    private fun getLocalParticipantListCellModel(localUserState: LocalUserState): ParticipantListCellModel {
        val localUserDisplayName = localUserState.displayName
        return ParticipantListCellModel(
            localUserDisplayName ?: "",
            localUserState.audioState.operation == AudioOperationalStatus.OFF,
            "",
            false
        )
    }

    private fun getRemoteParticipantListCellModel(it: ParticipantInfoModel): ParticipantListCellModel {
        return ParticipantListCellModel(
            it.displayName.trim(), it.isMuted,
            it.userIdentifier,
            it.participantStatus == ParticipantStatus.HOLD,
            it.participantStatus,
        )
    }
}

internal data class ParticipantListCellModel(
    val displayName: String,
    val isMuted: Boolean,
    val userIdentifier: String,
    val isOnHold: Boolean,
    val status: ParticipantStatus? = null,
)
