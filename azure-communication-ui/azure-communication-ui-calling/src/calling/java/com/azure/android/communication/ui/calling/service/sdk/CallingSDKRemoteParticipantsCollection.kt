// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.RemoteParticipant

internal interface CallingSDKRemoteParticipantsCollection {
    fun getRemoteParticipantsMap(): Map<String, RemoteParticipant>
}
