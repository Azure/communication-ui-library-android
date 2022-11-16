// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.ChatInfoModel
import com.azure.android.communication.ui.chat.models.LocalParticipantInfoModel
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel

// ChatStatus will help to subscribe to real tim notifications when state is initialized
// The foreground/background mode for activity can query as per state here
internal enum class ChatStatus {
    NONE,
    INITIALIZATION,
    INITIALIZED
}

internal data class ChatState(
    val chatStatus: ChatStatus,
    val localParticipantInfoModel: LocalParticipantInfoModel,
    val chatInfoModel: ChatInfoModel,
    val lastReadMessageId: String,
    val lastSendMessageId: String,
    val messageContextMenu: MessageContextMenuModel,
)
