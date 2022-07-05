// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.mocking.TestCallingSDKWrapper
import com.azure.android.communication.mocking.TestVideoViewManager
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Callable
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ContextInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val callComposite = CallCompositeBuilder().build()

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(UrlTokenFetcher(), true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val remoteOptions =
            CallCompositeRemoteOptions(CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
                communicationTokenCredential,
                "test")

        callComposite.launchTest(appContext, remoteOptions, null, TestCallingSDKWrapper(
            CoroutineContextProvider()), TestVideoViewManager(appContext))

        Thread.sleep(90000)
    }

    class UrlTokenFetcher() : Callable<String> {
        override fun call(): String {
            return "token"
        }
    }
}
