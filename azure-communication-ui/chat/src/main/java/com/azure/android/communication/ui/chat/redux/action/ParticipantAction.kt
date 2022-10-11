// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.models.RemoteParticipantsInfoModel

internal sealed class ParticipantAction : Action {
    class ParticipantsAdded(val participants: RemoteParticipantsInfoModel) : ParticipantAction()
    class ParticipantsRemoved(val participants: RemoteParticipantsInfoModel) : ParticipantAction()
}
