// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import com.azure.android.communication.ui.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.LocalUserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantListViewModel(private val avatarViewManager: AvatarViewManager) {

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
        getLocalParticipantPersonaDataWithSuffix(suffix)
    )

    fun init(participantMap: Map<String, ParticipantInfoModel>, localUserState: LocalUserState) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                ParticipantListCellModel(
                    it.displayName.trim(),
                    it.isMuted,
                    getLocalParticipantPersonaData()
                )
            }
        remoteParticipantListCellStateFlow = MutableStateFlow(remoteParticipantList)
        localParticipantListCellStateFlow =
            MutableStateFlow(
                ParticipantListCellModel(
                    localUserState.displayName ?: "",
                    localUserState.audioState.operation == AudioOperationalStatus.OFF,
                    getLocalParticipantPersonaData()
                )
            )
    }

    fun update(participantMap: Map<String, ParticipantInfoModel>, localUserState: LocalUserState) {
        val remoteParticipantList: List<ParticipantListCellModel> =
            participantMap.values.map {
                ParticipantListCellModel(
                    it.displayName.trim(),
                    it.isMuted,
                    avatarViewManager.getRemoteParticipantPersonaData(it.userIdentifier)
                )
            }
        remoteParticipantListCellStateFlow.value = remoteParticipantList
        localParticipantListCellStateFlow.value =
            ParticipantListCellModel(
                localUserState.displayName ?: "",
                localUserState.audioState.operation == AudioOperationalStatus.OFF,
                getLocalParticipantPersonaData()
            )
    }

    fun displayParticipantList() {
        displayParticipantListStateFlow.value = true
    }

    fun closeParticipantList() {
        displayParticipantListStateFlow.value = false
    }

    private fun getLocalParticipantPersonaData() =
        avatarViewManager.communicationUILocalDataOptions?.personaData

    private fun getLocalParticipantPersonaDataWithSuffix(suffix: String): CommunicationUIPersonaData? {
        avatarViewManager.communicationUILocalDataOptions?.personaData?.let {
            var displayName: String? = null
            it.renderedDisplayName?.let { renderedDisplayName ->
                displayName = "$renderedDisplayName $suffix"
            }
            return CommunicationUIPersonaData(displayName, it.avatarBitmap, it.scaleType)
        }
        return null
    }
}

internal data class ParticipantListCellModel(
    val displayName: String,
    val isMuted: Boolean,
    val personaData: CommunicationUIPersonaData?,
)
