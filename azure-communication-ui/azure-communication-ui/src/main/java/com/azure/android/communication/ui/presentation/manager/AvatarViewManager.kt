// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.configuration.CommunicationUILocalDataOptions

internal class AvatarViewManager(private val communicationUILocalDataOptions: CommunicationUILocalDataOptions?) {
    fun getLocalParticipantConfiguration() = communicationUILocalDataOptions
}
