// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureState
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantGridViewModel(
    private val participantGridCellViewModelFactory: ParticipantGridCellViewModelFactory,
    private val maxRemoteParticipantSize: Int
) {

    private var remoteParticipantsUpdatedStateFlow: MutableStateFlow<List<ParticipantGridCellViewModel>> =
        MutableStateFlow(mutableListOf())

    private var displayedRemoteParticipantsViewModelMap =
        mutableMapOf<String, ParticipantGridCellViewModel>()

    private var updateVideoStreamsCallback: ((List<Pair<String, String>>) -> Unit)? = null
    private var remoteParticipantStateModifiedTimeStamp: Number = 0
    private var pipStatus: PictureInPictureStatus = PictureInPictureStatus.NONE
    private lateinit var isLobbyOverlayDisplayedFlow: MutableStateFlow<Boolean>

    fun init(
        callingStatus: CallingStatus,
    ) {
        isLobbyOverlayDisplayedFlow = MutableStateFlow(isLobbyOverlayDisplayed(callingStatus))
    }

    fun clear() {
        remoteParticipantStateModifiedTimeStamp = 0
        displayedRemoteParticipantsViewModelMap.clear()
        remoteParticipantsUpdatedStateFlow.value = mutableListOf()
    }

    fun getRemoteParticipantsUpdateStateFlow(): StateFlow<List<ParticipantGridCellViewModel>> {
        return remoteParticipantsUpdatedStateFlow
    }

    fun setUpdateVideoStreamsCallback(callback: (List<Pair<String, String>>) -> Unit) {
        this.updateVideoStreamsCallback = callback
    }

    fun getMaxRemoteParticipantsSize(): Int {
        return if (pipStatus == PictureInPictureStatus.NONE)
            maxRemoteParticipantSize else 1
    }

    fun getIsLobbyOverlayDisplayedFlow(): StateFlow<Boolean> = isLobbyOverlayDisplayedFlow

    fun updateIsLobbyOverlayDisplayed(callingStatus: CallingStatus) {
        isLobbyOverlayDisplayedFlow.value = isLobbyOverlayDisplayed(callingStatus)
    }

    fun update(
        participantStateUpdatedTimestamp: Number,
        remoteParticipantsMap: Map<String, ParticipantInfoModel>,
        pipStatus: PictureInPictureStatus,
    ) {
        if (participantStateUpdatedTimestamp == remoteParticipantStateModifiedTimeStamp &&
            this.pipStatus == pipStatus
        ) {
            return
        }

        remoteParticipantStateModifiedTimeStamp = participantStateUpdatedTimestamp
        this.pipStatus = pipStatus

        var remoteParticipantsMapSorted = remoteParticipantsMap
        val participantSharingScreen = getParticipantSharingScreen(remoteParticipantsMap)

        if (participantSharingScreen.isNullOrEmpty()) {
            if (remoteParticipantsMap.size > getMaxRemoteParticipantsSize()) {
                remoteParticipantsMapSorted =
                    sortRemoteParticipantsByTimestamp(remoteParticipantsMap)
            }
        } else {
            remoteParticipantsMapSorted = mapOf(
                Pair(
                    participantSharingScreen,
                    remoteParticipantsMap[participantSharingScreen]!!
                )
            )
        }

        updateRemoteParticipantsVideoStreams(remoteParticipantsMapSorted)

        updateDisplayedParticipants(remoteParticipantsMapSorted.toMutableMap())
    }

    private fun getParticipantSharingScreen(
        remoteParticipantsMap: Map<String, ParticipantInfoModel>,
    ): String? {
        remoteParticipantsMap.forEach { (id, participantInfoModel) ->
            if (participantInfoModel.screenShareVideoStreamModel != null) {
                return id
            }
        }
        return null
    }

    private fun updateDisplayedParticipants(
        remoteParticipantsMapSorted: MutableMap<String, ParticipantInfoModel>,
    ) {
        val alreadyDisplayedParticipants =
            displayedRemoteParticipantsViewModelMap.filter { (id, _) ->
                remoteParticipantsMapSorted.containsKey(id)
            }

        val viewModelsThatCanBeRemoved = displayedRemoteParticipantsViewModelMap.keys.filter {
            !remoteParticipantsMapSorted.containsKey(it)
        }.toMutableList()

        alreadyDisplayedParticipants.forEach { (id, participantViewModel) ->
            if (participantViewModel.getParticipantModifiedTimestamp()
                != remoteParticipantsMapSorted[id]!!.modifiedTimestamp
            ) {
                participantViewModel.update(
                    remoteParticipantsMapSorted[id]!!,
                )
            }
            remoteParticipantsMapSorted.remove(id)
        }

        val viewModelsToRemoveCount =
            viewModelsThatCanBeRemoved.size - remoteParticipantsMapSorted.size

        if (viewModelsToRemoveCount > 0) {
            val keysToRemove = viewModelsThatCanBeRemoved.takeLast(viewModelsToRemoveCount)
            displayedRemoteParticipantsViewModelMap.keys.removeAll(keysToRemove)
            viewModelsThatCanBeRemoved.removeAll(keysToRemove)
        }

        if (viewModelsThatCanBeRemoved.isNotEmpty()) {
            val listToPreserveOrder =
                displayedRemoteParticipantsViewModelMap.toList().toMutableList()
            viewModelsThatCanBeRemoved.forEach {
                val indexToSwap = displayedRemoteParticipantsViewModelMap.keys.indexOf(it)
                val viewModel = displayedRemoteParticipantsViewModelMap[it]
                listToPreserveOrder.removeAt(indexToSwap)
                val participantID = remoteParticipantsMapSorted.keys.first()
                val participantInfoModel = remoteParticipantsMapSorted[participantID]
                viewModel?.update(participantInfoModel!!)
                listToPreserveOrder.add(indexToSwap, Pair(participantID, viewModel!!))
                remoteParticipantsMapSorted.remove(participantID)
            }
            displayedRemoteParticipantsViewModelMap.clear()
            listToPreserveOrder.forEach {
                displayedRemoteParticipantsViewModelMap[it.first] = it.second
            }
        }

        remoteParticipantsMapSorted.forEach { (id, participantInfoModel) ->
            displayedRemoteParticipantsViewModelMap[id] =
                participantGridCellViewModelFactory.ParticipantGridCellViewModel(
                    participantInfoModel,
                )
        }

        if (remoteParticipantsMapSorted.isNotEmpty() || viewModelsToRemoveCount > 0) {
            remoteParticipantsUpdatedStateFlow.value =
                displayedRemoteParticipantsViewModelMap.values.toList()
        }
    }

    private fun sortRemoteParticipantsByTimestamp(
        remoteParticipantsMap: Map<String, ParticipantInfoModel>,
    ): Map<String, ParticipantInfoModel> {
        return remoteParticipantsMap.toList()
            .sortedByDescending { it.second.speakingTimestamp.toLong() }
            .take(getMaxRemoteParticipantsSize()).toMap()
    }

    private fun updateRemoteParticipantsVideoStreams(
        participantViewModelMap: Map<String, ParticipantInfoModel>,
    ) {
        val usersVideoStream: MutableList<Pair<String, String>> = mutableListOf()
        participantViewModelMap.forEach { (participantId, participant) ->
            participant.cameraVideoStreamModel?.let {
                usersVideoStream.add(
                    Pair(
                        participantId,
                        participant.cameraVideoStreamModel!!.videoStreamID
                    )
                )
            }
            participant.screenShareVideoStreamModel?.let {
                usersVideoStream.add(
                    Pair(
                        participantId,
                        participant.screenShareVideoStreamModel!!.videoStreamID
                    )
                )
            }
        }
        updateVideoStreamsCallback?.invoke(usersVideoStream)
    }

    private fun isLobbyOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY
}
