// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions

// Helper functions that access internal UI chat API.
// These must reside in `com.azure.android.communication.ui.chat`

internal fun launchChatComposite() {

    val communicationTokenRefreshOptions = CommunicationTokenRefreshOptions({ "token" }, true)
    val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
    val chatAdapter = ChatAdapterBuilder()
        .endpointUrl("https://acs-ui-dev.communication.azure.com/")
        .communicationTokenCredential(communicationTokenCredential)
        .identity("test")
        .build()

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    chatAdapter.launchTest(appContext, "19:lSNju7o5X9EYJInIIxkJQw1TMnllGMytNCtvhYCxvpE1@thread.v2")
}
