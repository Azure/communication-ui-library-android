// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID
import com.azure.android.communication.assertViewGone
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions

@OptIn(ExperimentalCoroutinesApi::class)
internal class AvModeTest : BaseUiTest() {

    @Test
    fun testAvModeDisablesCameraButtons() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
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
                "test"
            )

        val localOptions = CallCompositeLocalOptions().setAudioVideoMode(CallCompositeAudioVideoMode.AUDIO_ONLY)

        callComposite.launchTest(appContext, remoteOptions, localOptions)
        waitUntilDisplayed(joinCallId)
        assertViewGone(setupCameraButtonId)
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        assertViewGone(callCameraButtonId)
    }
}
