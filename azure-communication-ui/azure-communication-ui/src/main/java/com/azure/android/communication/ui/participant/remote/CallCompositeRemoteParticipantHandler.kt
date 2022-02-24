// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.remote

internal class CallCompositeRemoteParticipantHandler {
    private var remoteParticipantHandler: CallingRemoteParticipantHandler? = null

    fun getOnCallingParticipantHandler() = remoteParticipantHandler

    fun setOnCallingRemoteParticipantHandler(remoteParticipantHandler: CallingRemoteParticipantHandler?) {
        this.remoteParticipantHandler = remoteParticipantHandler
    }
}
