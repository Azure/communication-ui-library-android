// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.remote

internal class CallCompositeRemoteParticipantHandler {
    private var remoteParticipantJoinedHandler: RemoteParticipantJoinedHandler? = null

    fun getOnCallingParticipantHandler() = remoteParticipantJoinedHandler

    fun setOnRemoteParticipantJoinedHandler(remoteParticipantJoinedHandler: RemoteParticipantJoinedHandler?) {
        this.remoteParticipantJoinedHandler = remoteParticipantJoinedHandler
    }
}
