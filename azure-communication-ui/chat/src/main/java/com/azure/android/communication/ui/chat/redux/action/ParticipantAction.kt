// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.models.ParticipantTimestampInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel

internal sealed class ParticipantAction : Action {
    class ParticipantsAdded(val participants: List<RemoteParticipantInfoModel>) :
        ParticipantAction()
    class ParticipantsRemoved(val participants: List<RemoteParticipantInfoModel>) :
        ParticipantAction()
    class TypingIndicatorReceived(val message: ParticipantTimestampInfoModel) : ParticipantAction()
    class TypingIndicatorClear(val message: ParticipantTimestampInfoModel) : ParticipantAction()
}
