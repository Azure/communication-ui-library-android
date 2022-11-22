// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.common.CommunicationTokenCredential

internal class ChatCompositeRemoteOptions internal constructor(
    val endpointUrl: String,
    val threadId: String,
    val credential: CommunicationTokenCredential,
    val identity: String,
    val displayName: String = ""
)
