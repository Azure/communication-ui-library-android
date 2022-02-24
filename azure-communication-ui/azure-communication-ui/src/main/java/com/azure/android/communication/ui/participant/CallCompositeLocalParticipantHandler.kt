// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant

internal class CallCompositeLocalParticipantHandler {
    private var localParticipantHandler: CallingLocalParticipantHandler? = null

    fun getOnCallingLocalParticipantHandler() = localParticipantHandler

    fun setOnCallingLocalParticipantHandler(localParticipantHandler: CallingLocalParticipantHandler?) {
        this.localParticipantHandler = localParticipantHandler
    }
}
