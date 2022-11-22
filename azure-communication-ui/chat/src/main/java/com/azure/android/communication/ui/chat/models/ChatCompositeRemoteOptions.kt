// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into

internal class ChatCompositeRemoteOptions internal constructor(
    val endpointUrl: String,
    val threadId: String,
    val credential: CommunicationTokenCredential,
    private val commonIdentity: CommunicationIdentifier,
    val displayName: String = "",
) {
    val identity: String by lazy { commonIdentity.into().id }
}
