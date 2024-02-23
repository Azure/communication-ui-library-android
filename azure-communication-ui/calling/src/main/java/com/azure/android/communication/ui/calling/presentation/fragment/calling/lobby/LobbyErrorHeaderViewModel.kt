// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class LobbyErrorHeaderViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var displayLobbyErrorHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var lobbyErrorFlow: MutableStateFlow<CallCompositeLobbyErrorCode?>

    fun getDisplayLobbyErrorHeaderFlow(): StateFlow<Boolean> = displayLobbyErrorHeaderFlow

    fun getLobbyErrorFlow(): StateFlow<CallCompositeLobbyErrorCode?> = lobbyErrorFlow

    fun update(
        callStatus: CallStatus,
        error: CallCompositeLobbyErrorCode?,
        canShowLobby: Boolean
    ) {
        displayLobbyErrorHeaderFlow.value = error != null &&
            callStatus == CallStatus.CONNECTED &&
            canShowLobby
        lobbyErrorFlow.value = error
    }

    fun init(
        callStatus: CallStatus,
        error: CallCompositeLobbyErrorCode?,
        canShowLobby: Boolean
    ) {
        displayLobbyErrorHeaderFlow = MutableStateFlow(
            error != null &&
                callStatus == CallStatus.CONNECTED &&
                canShowLobby
        )
        lobbyErrorFlow = MutableStateFlow(error)
    }

    fun close() {
        dispatch(ParticipantAction.ClearLobbyError())
    }

    fun dismiss() {
        displayLobbyErrorHeaderFlow.value = false
    }
}
