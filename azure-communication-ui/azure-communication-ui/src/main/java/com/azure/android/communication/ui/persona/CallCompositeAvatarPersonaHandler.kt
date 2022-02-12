// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona

internal class CallCompositeAvatarPersonaHandler {
    private var participantHandler: CallingParticipantHandler? = null

    fun getOnAvatarPersonaHandler() = participantHandler

    fun setOnAvatarPersonaHandler(participantHandler: CallingParticipantHandler?) {
        this.participantHandler = participantHandler
    }
}
