// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.LocalParticipantInfoModel

internal data class ParticipantsState(
    val participantMap: Map<String, LocalParticipantInfoModel>,
)
