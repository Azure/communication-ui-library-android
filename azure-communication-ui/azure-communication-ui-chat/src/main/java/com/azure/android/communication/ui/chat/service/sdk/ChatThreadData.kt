// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential

internal class ChatThreadData(
    val name: String,
    val identifier: CommunicationIdentifier,
    val communicationTokenCredential: CommunicationTokenCredential,
    val threadId: String,
    val url: String,
    val applicationID: String,
    val sdkName: String,
    val sdkVersion: String,
)
