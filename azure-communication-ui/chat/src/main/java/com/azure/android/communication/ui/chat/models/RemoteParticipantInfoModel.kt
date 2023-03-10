// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier

internal data class RemoteParticipantInfoModel(
    val userIdentifier: CommunicationIdentifier,
    val displayName: String?,
    val isLocalUser: Boolean = false
)
