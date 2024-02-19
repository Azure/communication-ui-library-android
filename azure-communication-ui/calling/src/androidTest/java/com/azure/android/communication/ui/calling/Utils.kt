// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import java.util.UUID

// Helper functions that access internal UI calling API.
// These must reside in `com.azure.android.communication.ui.calling`

internal fun launchComposite() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val callComposite = CallCompositeBuilder().build()
    val communicationTokenRefreshOptions =
        CommunicationTokenRefreshOptions({ "token" }, true)
    val communicationTokenCredential =
        CommunicationTokenCredential(communicationTokenRefreshOptions)
    val remoteOptions =
        CallCompositeRemoteOptions(
            CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
            communicationTokenCredential,
            "test",
        )

    callComposite.launchTest(appContext, remoteOptions, null)
}
