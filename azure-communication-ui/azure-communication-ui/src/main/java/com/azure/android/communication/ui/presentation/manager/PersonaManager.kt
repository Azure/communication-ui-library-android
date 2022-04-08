// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration
import com.azure.android.communication.ui.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfigurationHandler

internal class PersonaManager(
    private val localParticipantConfiguration: LocalParticipantConfiguration?,
    private val remoteParticipantsConfiguration: RemoteParticipantsConfiguration,
) : RemoteParticipantsConfigurationHandler {

    init {
        remoteParticipantsConfiguration.setRemoteParticipantsConfigurationHandler(this)
    }

    fun getLocalParticipantConfiguration() = localParticipantConfiguration

    override fun onSetRemoteParticipantPersonaData(data: RemoteParticipantPersonaData) {
    }
}
