// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.common.CommunicationUserIdentifier

// Helper functions that access internal UI chat API.
// These must reside in `com.azure.android.communication.ui.chat`

internal fun launchChatComposite() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val communicationTokenRefreshOptions = CommunicationTokenRefreshOptions({ "token" }, true)
    val communicationTokenCredential =
        CommunicationTokenCredential(communicationTokenRefreshOptions)
    val chatAdapter =
        ChatAdapterBuilder()
            .endpoint("https://acs-ui-dev.communication.azure.com/")
            .credential(communicationTokenCredential)
            .identity(CommunicationUserIdentifier("test"))
            .threadId("19:lSNju7o5X9EYJInIIxkJQw1TMnllGMytNCtvhYCxvpE1@thread.v2")
            .build()

    chatAdapter.connect(appContext)
    chatAdapter.showTestCompositeUI(appContext)
}
