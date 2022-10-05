// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.common.CommunicationTokenCredential

internal class ChatConfiguration(
    val endPointURL: String,
    val identity: String,
    val credential: CommunicationTokenCredential,
    val applicationID: String,
    val sdkName: String,
    val sdkVersion: String,
    val threadId: String,
    val senderDisplayName: String,
)
