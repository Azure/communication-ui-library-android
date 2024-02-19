// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel

internal sealed class ParticipantAction : Action {
    class ListUpdated(val participantMap: Map<String, ParticipantInfoModel>) : ParticipantAction()

    class DominantSpeakersUpdated(val dominantSpeakersInfo: List<String>) : ParticipantAction()

    class AdmitAll : ParticipantAction()

    class Admit(val userIdentifier: String) : ParticipantAction()

    class Decline(val userIdentifier: String) : ParticipantAction()

    class LobbyError(val code: CallCompositeLobbyErrorCode) : ParticipantAction()

    class ClearLobbyError : ParticipantAction()
}
