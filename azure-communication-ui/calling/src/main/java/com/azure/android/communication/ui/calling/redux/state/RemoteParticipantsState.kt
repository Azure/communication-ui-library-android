// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.DominantSpeakersInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel

internal data class RemoteParticipantsState(
    val participantMap: Map<String, ParticipantInfoModel>,
    val participantMapModifiedTimestamp: Number,
    val dominantSpeakersInfo: DominantSpeakersInfoModel,
)
