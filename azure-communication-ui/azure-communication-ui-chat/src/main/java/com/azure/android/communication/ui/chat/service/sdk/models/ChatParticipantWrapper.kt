// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import com.azure.android.communication.ui.chat.service.sdk.into
import org.threeten.bp.OffsetDateTime

internal interface ChatParticipant {
    val communicationIdentifier: CommunicationIdentifier
    val displayName: String
    val shareHistoryTime: OffsetDateTime
}

internal class ChatParticipantWrapper(chatParticipant: com.azure.android.communication.chat.models.ChatParticipant) :
    ChatParticipant {
    override val communicationIdentifier: CommunicationIdentifier =
        chatParticipant.communicationIdentifier.into()
    override val displayName: String = chatParticipant.displayName
    override val shareHistoryTime: OffsetDateTime = chatParticipant.shareHistoryTime
}
