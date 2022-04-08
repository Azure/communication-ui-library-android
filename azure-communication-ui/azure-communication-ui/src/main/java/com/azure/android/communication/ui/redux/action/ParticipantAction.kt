// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.action

import com.azure.android.communication.ui.model.ParticipantInfoModel

internal sealed class ParticipantAction : Action {
    class ListUpdated(val participantMap: Map<String, ParticipantInfoModel>) : ParticipantAction()
}
