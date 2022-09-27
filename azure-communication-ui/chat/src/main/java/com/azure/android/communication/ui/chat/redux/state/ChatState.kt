// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.ChatInfoModel
import com.azure.android.communication.ui.chat.models.ParticipantInfoModel

internal enum class ChatStatus {
    NONE,
    INITIALIZATION,
    INITIALIZED,
}

internal data class ChatState(
    val chatStatus: ChatStatus,
    val localParticipantInfoModel: ParticipantInfoModel,
    val chatInfoModel: ChatInfoModel,
)
