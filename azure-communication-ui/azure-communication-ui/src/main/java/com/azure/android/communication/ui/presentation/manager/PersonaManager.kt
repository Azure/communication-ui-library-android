// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration

internal class PersonaManager(private val localParticipantConfiguration: LocalParticipantConfiguration?) {
    fun getLocalParticipantConfiguration() = localParticipantConfiguration
}
