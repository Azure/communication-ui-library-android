// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.model.ParticipantInfoModel

internal data class RemoteParticipantsState(
    val participantMap: Map<String, ParticipantInfoModel>,
    val modifiedTimestamp: Number,
)
