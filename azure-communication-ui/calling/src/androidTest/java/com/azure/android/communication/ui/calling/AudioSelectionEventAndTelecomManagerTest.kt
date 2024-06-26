// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapOnText
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionMode
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerOptions
import com.azure.android.communication.waitUntilDisplayed
import com.azure.android.communication.waitUntilTextDisplayed
import java.util.UUID
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class AudioSelectionEventAndTelecomManagerTest : BaseUiTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAudioSelectionEventWithoutTelecomManager() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder()
            .credential(communicationTokenCredential)
            .applicationContext(appContext)
            .displayName("test")
            .build()

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var audioDeviceChanged: CallCompositeAudioSelectionChangedEvent? = null
        val audioDeviceChangedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnAudioSelectionChangedEventHandler {
            audioDeviceChanged = it
            audioDeviceChangedCompletableFuture.complete(null)
        }

        callComposite.launchTest(appContext, CallCompositeGroupCallLocator(UUID.fromString(groupCallTestGUID)), null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        audioDeviceChangedCompletableFuture.get()

        assert(audioDeviceChanged?.audioSelectionMode == CallCompositeAudioSelectionMode.SPEAKER)

        callComposite.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAudioSelectionEventWithoutTelecomManager_audioDeviceChange() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder()
            .build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString(groupCallTestGUID)),
                communicationTokenCredential,
                "test"
            )

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var audioDeviceChanged: CallCompositeAudioSelectionChangedEvent? = null
        val audioDeviceChangedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnAudioSelectionChangedEventHandler {
            audioDeviceChanged = it
            audioDeviceChangedCompletableFuture.complete(null)
        }

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        tapWhenDisplayed(audioDeviceSelectionButtonId)
        waitUntilTextDisplayed(androidAudioDevice)
        tapOnText(androidAudioDevice)
        waitUntilDisplayed(endCallId)

        assert(callComposite.callState == CallCompositeCallStateCode.CONNECTED)

        audioDeviceChangedCompletableFuture.get()

        assert(audioDeviceChanged?.audioSelectionMode == CallCompositeAudioSelectionMode.RECEIVER)

        callComposite.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAudioSelectionEventWithTelecomManager() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder()
            .telecomManagerOptions(
                CallCompositeTelecomManagerOptions(
                    CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
                    "test"
                )
            )
            .build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString(groupCallTestGUID)),
                communicationTokenCredential,
                "test"
            )

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var audioDeviceChanged: CallCompositeAudioSelectionChangedEvent? = null
        val audioDeviceChangedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnAudioSelectionChangedEventHandler {
            audioDeviceChanged = it
            audioDeviceChangedCompletableFuture.complete(null)
        }

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        assert(callComposite.callState == CallCompositeCallStateCode.CONNECTED)

        audioDeviceChangedCompletableFuture.get()

        assert(audioDeviceChanged?.audioSelectionMode == CallCompositeAudioSelectionMode.SPEAKER)

        callComposite.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAudioSelectionEventWithTelecomManager_and_skipSetupOn() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder()
            .telecomManagerOptions(
                CallCompositeTelecomManagerOptions(
                    CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
                    "test"
                )
            )
            .build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString(groupCallTestGUID)),
                communicationTokenCredential,
                "test"
            )

        var audioDeviceChanged: CallCompositeAudioSelectionChangedEvent? = null
        val audioDeviceChangedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnAudioSelectionChangedEventHandler {
            audioDeviceChanged = it
            audioDeviceChangedCompletableFuture.complete(null)
        }

        val localOptions = CallCompositeLocalOptions().setSkipSetupScreen(true)

        callComposite.launchTest(appContext, remoteOptions, localOptions)

        waitUntilDisplayed(endCallId)

        audioDeviceChangedCompletableFuture.get()

        assert(audioDeviceChanged?.audioSelectionMode == CallCompositeAudioSelectionMode.SPEAKER)

        callComposite.dismiss()
    }
}
