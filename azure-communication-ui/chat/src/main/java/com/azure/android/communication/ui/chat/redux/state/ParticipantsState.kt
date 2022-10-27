// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel

internal data class ParticipantsState(
    val participants: Map<String, RemoteParticipantInfoModel>,
    val participantTyping: Map<String, String>
)
