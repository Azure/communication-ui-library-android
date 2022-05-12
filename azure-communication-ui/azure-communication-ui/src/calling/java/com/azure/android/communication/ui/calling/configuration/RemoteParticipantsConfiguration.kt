// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.models.ParticipantViewData
import com.azure.android.communication.ui.calling.models.SetParticipantViewDataResult

internal data class RemoteParticipantViewData(
    val identifier: CommunicationIdentifier,
    val participantViewData: ParticipantViewData,
)

internal interface RemoteParticipantsConfigurationHandler {
    fun onSetParticipantViewData(data: RemoteParticipantViewData): SetParticipantViewDataResult
    fun onRemoveParticipantViewData(identifier: String)
}

internal class RemoteParticipantsConfiguration {
    private var handler: RemoteParticipantsConfigurationHandler? = null

    fun setHandler(handler: RemoteParticipantsConfigurationHandler) {
        this.handler = handler
    }

    fun setParticipantViewData(
        identifier: CommunicationIdentifier,
        participantViewData: ParticipantViewData,
    ): SetParticipantViewDataResult {
        handler?.let {
            return@setParticipantViewData it.onSetParticipantViewData(
                RemoteParticipantViewData(identifier, participantViewData)
            )
        }
        return SetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL
    }

    fun removeParticipantViewData(identifier: String) {
        handler?.onRemoveParticipantViewData(identifier)
    }
}
