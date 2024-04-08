// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel

internal data class RemoteParticipantsState(
    val participantMap: Map<String, ParticipantInfoModel>,
    val participantMapModifiedTimestamp: Number,
    val dominantSpeakersInfo: List<String>,
    val dominantSpeakersModifiedTimestamp: Number,
    val lobbyErrorCode: CallCompositeLobbyErrorCode?
)
