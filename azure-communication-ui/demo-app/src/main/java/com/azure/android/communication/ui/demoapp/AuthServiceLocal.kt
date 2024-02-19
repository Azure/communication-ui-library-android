// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.demoapp

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier

internal class AuthServiceLocal(
    override val currentUserToken: String,
    private val communicationUserId: String,
) : AuthService {
    override val currentUserCommunicationIdentifier: CommunicationIdentifier
        by lazy { CommunicationUserIdentifier(communicationUserId) }

    override fun tokenRefresher(): String {
        return currentUserToken
    }

    override suspend fun ensureAuthInfo() {
    }
}
