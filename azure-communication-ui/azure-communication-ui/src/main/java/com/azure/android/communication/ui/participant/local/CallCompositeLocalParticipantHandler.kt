// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.local

internal class CallCompositeLocalParticipantHandler {
    private var localParticipantInitializedHandler: LocalParticipantInitializedHandler? = null

    fun getOnCallingLocalParticipantHandler() = localParticipantInitializedHandler

    fun setOnLocalParticipantInitializedHandler(localParticipantInitializedHandler: LocalParticipantInitializedHandler?) {
        this.localParticipantInitializedHandler = localParticipantInitializedHandler
    }
}
