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

internal class ParticipantListViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private val localUserIdentifier = ""
    private lateinit var vvmMutableStateFlow: MutableStateFlow<ParticipantListViewViewModel>
    private lateinit var localParticipantListCellMutableFlow: MutableStateFlow<ParticipantListCellModel>
    private val displayParticipantListMutableStateFlow = MutableStateFlow(false)
    private lateinit var displayParticipantMenuCallback: (userIdentifier: String, displayName: String?) -> Unit

    val vvmStateFlow: StateFlow<ParticipantListViewViewModel> get() = vvmMutableStateFlow

//    val localParticipantListCellStateFlow: StateFlow<ParticipantListCellModel> get() =
//        localParticipantListCellMutableFlow

    fun createLocalParticipantListCellViewModel(suffix: String) = ParticipantListCellModel(
        (localParticipantListCellMutableFlow.value.displayName.trim() + " " + suffix).trim(),
        localParticipantListCellMutableFlow.value.isMuted,
        "",
        false
    )

    fun init(
        participantMap: Map<String, ParticipantInfoModel>,
        localUserState: LocalUserState,
        canShowLobby: Boolean,
        displayParticipantMenuCallback: (userIdentifier: String, displayName: String?) -> Unit,
        totalParticipantCountExceptHidden: Int,
    ) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                getRemoteParticipantListCellModel(it)
            }.filter { participantListRemoteParticipantVisibility(it, canShowLobby) }
        vvmMutableStateFlow = MutableStateFlow(
            ParticipantListViewViewModel(
                remoteParticipantList = remoteParticipantList,
                localParticipantListCell = getLocalParticipantListCellModel(localUserState),
                totalActiveParticipantCount = totalParticipantCountExceptHidden,
                isDisplayed = false
            ))

        localParticipantListCellMutableFlow =
            MutableStateFlow(getLocalParticipantListCellModel(localUserState))

        this.displayParticipantMenuCallback = displayParticipantMenuCallback
    }

    fun update(
        participantMap: Map<String, ParticipantInfoModel>,
        localUserState: LocalUserState,
        visibilityState: VisibilityState,
        canShowLobby: Boolean,
        totalParticipantCountExceptHidden: Int,
    ) {
        val remoteParticipantList: MutableList<ParticipantListCellModel> =
            participantMap.values.map {
                getRemoteParticipantListCellModel(it)
            }.filter { participantListRemoteParticipantVisibility(it, canShowLobby) }.toMutableList()

        val display = if (visibilityState.status != VisibilityStatus.VISIBLE)
            false
        else vvmMutableStateFlow.value.isDisplayed

        vvmMutableStateFlow.value = ParticipantListViewViewModel(
            remoteParticipantList = remoteParticipantList,
            localParticipantListCell = getLocalParticipantListCellModel(localUserState),
            totalActiveParticipantCount = totalParticipantCountExceptHidden,
            isDisplayed = display,
        )
    }

    private fun participantListRemoteParticipantVisibility(
        it: ParticipantListCellModel,
        canShowLobby: Boolean,
    ) = (
        it.status != ParticipantStatus.DISCONNECTED &&
            if (it.status == ParticipantStatus.IN_LOBBY) canShowLobby else true
        )

    fun displayParticipantList() {
        vvmMutableStateFlow.value = vvmMutableStateFlow.value.copy(isDisplayed = true)
    }

    fun closeParticipantList() {
        vvmMutableStateFlow.value = vvmMutableStateFlow.value.copy(isDisplayed = false)
    }

    fun admitParticipant(userIdentifier: String) {
        dispatch(ParticipantAction.Admit(userIdentifier))
    }

    fun declineParticipant(userIdentifier: String) {
        dispatch(ParticipantAction.Reject(userIdentifier))
    }

    fun admitAllLobbyParticipants() {
        dispatch(ParticipantAction.AdmitAll())
    }

    fun displayParticipantMenu(userIdentifier: String, displayName: String?) {
        if (userIdentifier != localUserIdentifier) {
            closeParticipantList()
            displayParticipantMenuCallback(userIdentifier, displayName)
        }
    }

    private fun getLocalParticipantListCellModel(localUserState: LocalUserState): ParticipantListCellModel {
        val localUserDisplayName = localUserState.displayName
        return ParticipantListCellModel(
            localUserDisplayName ?: "",
            localUserState.audioState.operation == AudioOperationalStatus.OFF,
            localUserIdentifier,
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

internal data class ParticipantListViewViewModel(
    val remoteParticipantList: List<ParticipantListCellModel>,
    val localParticipantListCell: ParticipantListCellModel,
    val totalActiveParticipantCount: Int,
    val isDisplayed: Boolean,
)

internal data class ParticipantListCellModel(
    val displayName: String,
    val isMuted: Boolean,
    val userIdentifier: String,
    val isOnHold: Boolean,
    val status: ParticipantStatus? = null,
)
