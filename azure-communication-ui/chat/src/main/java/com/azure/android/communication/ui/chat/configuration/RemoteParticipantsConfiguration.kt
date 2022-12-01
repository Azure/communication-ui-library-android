// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.models.ChatCompositeParticipantViewData
import com.azure.android.communication.ui.chat.models.ChatCompositeSetParticipantViewDataResult
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier

internal class RemoteParticipantsConfiguration {
    fun setParticipantViewData(
        identifier: CommunicationIdentifier,
        participantViewData: ChatCompositeParticipantViewData,
    ): ChatCompositeSetParticipantViewDataResult {

        return ChatCompositeSetParticipantViewDataResult.PARTICIPANT_NOT_IN_CHAT
    }
}
