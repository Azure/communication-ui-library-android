// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.DeviceConfigurationState
import com.azure.android.communication.ui.calling.redux.state.RttState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import com.azure.android.communication.ui.calling.utilities.EventFlow
import com.azure.android.communication.ui.calling.utilities.MutableEventFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Integer.min

internal class ParticipantGridViewModel(
    private val participantGridCellViewModelFactory: ParticipantGridCellViewModelFactory,
    private val maxRemoteParticipantSize: Int,
) {

    private var remoteParticipantsUpdatedStateFlow: MutableStateFlow<List<ParticipantGridCellViewModel>> =
        MutableStateFlow(mutableListOf())

    private var displayedRemoteParticipantsViewModelMap =
        mutableMapOf<String, ParticipantGridCellViewModel>()

    private val mutableParticipantUpdated = MutableEventFlow()

    private var updateVideoStreamsCallback: ((List<Pair<String, String>>) -> Unit)? = null
    private var remoteParticipantStateModifiedTimeStamp: Number = 0
    private var dominantSpeakersStateModifiedTimestamp: Number = 0
    private var visibilityStatus: VisibilityStatus? = null
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var isVerticalStyleGridMutableFlow: MutableStateFlow<Boolean>

    val isVerticalStyleGridFlow: StateFlow<Boolean>
        get() = isVerticalStyleGridMutableFlow

    val participantUpdated: EventFlow = mutableParticipantUpdated

    fun init(
        rttState: RttState,
        isOverlayDisplayedOverGrid: Boolean,
        deviceConfigurationState: DeviceConfigurationState,
        captionsState: CaptionsState,
    ) {
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayedOverGrid)
        isVerticalStyleGridMutableFlow = MutableStateFlow(
            shouldUseVerticalStyleGrid(deviceConfigurationState, rttState, captionsState)
        )
    }

    fun clear() {
        remoteParticipantStateModifiedTimeStamp = 0
        dominantSpeakersStateModifiedTimestamp = 0
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
        return if (visibilityStatus == VisibilityStatus.VISIBLE)
            maxRemoteParticipantSize else 1
    }

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun update(
        remoteParticipantsMapUpdatedTimestamp: Number,
        remoteParticipantsMap: Map<String, ParticipantInfoModel>,
        dominantSpeakersInfo: List<String>,
        dominantSpeakersModifiedTimestamp: Number,
        visibilityStatus: VisibilityStatus,
        rttState: RttState,
        isOverlayDisplayedOverGrid: Boolean,
        deviceConfigurationState: DeviceConfigurationState,
        captionsState: CaptionsState,
    ) {

        isOverlayDisplayedFlow.value = isOverlayDisplayedOverGrid
        isVerticalStyleGridMutableFlow.value = shouldUseVerticalStyleGrid(deviceConfigurationState, rttState, captionsState)

        if (remoteParticipantsMapUpdatedTimestamp == remoteParticipantStateModifiedTimeStamp &&
            dominantSpeakersModifiedTimestamp == dominantSpeakersStateModifiedTimestamp &&
            this.visibilityStatus == visibilityStatus
        ) {
            return
        }

        remoteParticipantStateModifiedTimeStamp = remoteParticipantsMapUpdatedTimestamp
        dominantSpeakersStateModifiedTimestamp = dominantSpeakersModifiedTimestamp
        this.visibilityStatus = visibilityStatus

        var remoteParticipantsMapSorted = remoteParticipantsMap
        val participantSharingScreen = getParticipantSharingScreen(remoteParticipantsMap)

        if (participantSharingScreen.isNullOrEmpty()) {
            if (remoteParticipantsMap.size > getMaxRemoteParticipantsSize()) {
                remoteParticipantsMapSorted =
                    sortRemoteParticipants(remoteParticipantsMap, dominantSpeakersInfo)
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

    private fun shouldUseVerticalStyleGrid(
        deviceConfigurationState: DeviceConfigurationState,
        rttState: RttState,
        captionsState: CaptionsState,
    ): Boolean {
        return deviceConfigurationState.isPortrait ||
            rttState.isRttActive ||
            captionsState.status == CaptionsStatus.STARTED ||
            captionsState.status == CaptionsStatus.START_REQUESTED
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

        // participants list may not be changed, but their state may be changed, like isMuted
        mutableParticipantUpdated.emit()
    }

    private fun sortRemoteParticipants(
        remoteParticipantsMap: Map<String, ParticipantInfoModel>,
        dominantSpeakersInfo: List<String>,
    ): Map<String, ParticipantInfoModel> {

        val dominantSpeakersOrder = mutableMapOf<String, Int>()

        for (i in 0 until min(maxRemoteParticipantSize, dominantSpeakersInfo.count())) {
            dominantSpeakersOrder[dominantSpeakersInfo[i]] = i
        }

        val lengthComparator = Comparator<Pair<String, ParticipantInfoModel>> { keyValuePair1, keyValuePair2 ->
            val participantId1 = keyValuePair1.first
            val participantId2 = keyValuePair2.first
            val participant1 = keyValuePair1.second
            val participant2 = keyValuePair2.second

            if (dominantSpeakersOrder.containsKey(participantId1) &&
                dominantSpeakersOrder.containsKey(participantId2)
            ) {
                val order1 = dominantSpeakersOrder.getValue(participantId1)
                val order2 = dominantSpeakersOrder.getValue(participantId2)
                return@Comparator if (order1 > order2)
                    1 else -1
            }

            if (dominantSpeakersOrder.containsKey(participantId1))
                return@Comparator -1

            if (dominantSpeakersOrder.containsKey(participantId2))
                return@Comparator 1

            if ((participant1.cameraVideoStreamModel != null && participant2.cameraVideoStreamModel != null) ||
                (participant1.cameraVideoStreamModel == null && participant2.cameraVideoStreamModel == null)
            )
                return@Comparator 0

            if (participant1.cameraVideoStreamModel != null)
                return@Comparator -1
            else
                return@Comparator 1
        }

        return remoteParticipantsMap.toList()
            .sortedWith(lengthComparator)
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
}
