// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.demoapp

import com.azure.android.communication.common.CommunicationIdentifier

internal interface AuthService {
    val currentUserToken: String
    val currentUserCommunicationIdentifier: CommunicationIdentifier

    fun tokenRefresher(): String

    suspend fun ensureAuthInfo()
}
