// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant

internal class CallCompositeParticipantHandler {
    private var participantHandler: CallingParticipantHandler? = null

    fun getOnCallingParticipantHandler() = participantHandler

    fun setOnCallingParticipantHandler(participantHandler: CallingParticipantHandler?) {
        this.participantHandler = participantHandler
    }
}
