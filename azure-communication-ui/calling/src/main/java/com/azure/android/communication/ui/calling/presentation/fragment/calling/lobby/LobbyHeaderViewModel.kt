// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class LobbyHeaderViewModel {
    private lateinit var displayLobbyHeaderFlow: MutableStateFlow<Boolean>
    private var lobbyParticipantsCache: Map<String, ParticipantInfoModel> = emptyMap()

    fun getDisplayLobbyHeaderFlow(): StateFlow<Boolean> = displayLobbyHeaderFlow

    fun update(
        callStatus: CallStatus,
        lobbyParticipants: Map<String, ParticipantInfoModel>,
        canShowLobby: Boolean,
    ) {
        var isNewLobbyParticipantAdded = isNewParticipantAdded(lobbyParticipants)
        displayLobbyHeaderFlow.value = lobbyParticipants.isNotEmpty() &&
            (isNewLobbyParticipantAdded || displayLobbyHeaderFlow.value) &&
            callStatus == CallStatus.CONNECTED && canShowLobby
    }

    fun init(
        callStatus: CallStatus,
        lobbyParticipants: Map<String, ParticipantInfoModel>,
        canShowLobby: Boolean,
    ) {
        var isNewLobbyParticipantAdded = isNewParticipantAdded(lobbyParticipants)
        displayLobbyHeaderFlow = MutableStateFlow(
            lobbyParticipants.isNotEmpty() &&
                (isNewLobbyParticipantAdded || displayLobbyHeaderFlow.value) &&
                callStatus == CallStatus.CONNECTED && canShowLobby
        )
    }

    private fun isNewParticipantAdded(
        lobbyParticipants: Map<String, ParticipantInfoModel>
    ): Boolean {
        var isNewLobbyParticipantAdded = false
        if (lobbyParticipantsCache.size < lobbyParticipants.size) {
            isNewLobbyParticipantAdded = true
        } else if (lobbyParticipantsCache.size >= lobbyParticipants.size) {
            // compare the two maps to see if the participant is removed and new one is added
            for (participant in lobbyParticipants) {
                if (!lobbyParticipantsCache.containsKey(participant.key)) {
                    isNewLobbyParticipantAdded = true
                    break
                }
            }
        }
        lobbyParticipantsCache = lobbyParticipants
        return isNewLobbyParticipantAdded
    }

    fun close() {
        if (displayLobbyHeaderFlow.value) {
            displayLobbyHeaderFlow.value = false
        }
    }

    fun dismiss() {
        // clearing cache will help to reopen on resume state
        if (displayLobbyHeaderFlow.value) {
            lobbyParticipantsCache = mapOf()
        }
        close()
    }
}
