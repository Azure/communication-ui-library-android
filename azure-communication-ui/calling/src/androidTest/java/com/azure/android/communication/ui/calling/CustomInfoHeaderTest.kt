// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertTextDisplayed
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapOnScreen
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenHeaderViewData
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class CustomInfoHeaderTest : BaseUiTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderTitleTextIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val header = "custom header"

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(
            CallCompositeCallScreenHeaderViewData()
                .setTitle(header)
        )
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(header)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderSubtitleTextIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val subtitle = "custom subtitle"

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(
            CallCompositeCallScreenHeaderViewData()
                .setSubtitle(subtitle)
        )
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(subtitle)
        assertTextDisplayed(appContext.getString(waitingForOthersToJoinString))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderTitleAndSubtitleTextIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val header = "custom header"
        val subtitle = "custom subtitle"

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(
            CallCompositeCallScreenHeaderViewData()
                .setTitle(header).setSubtitle(subtitle)
        )
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(header)
        assertTextDisplayed(subtitle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderTitleUpdateWithNoInitialSet() = runTest {
        injectDependencies(testScheduler)

        val header = "custom header"
        val headerOptions =
            CallCompositeCallScreenHeaderViewData()

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(headerOptions)
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(appContext.getString(waitingForOthersToJoinString))
        tapOnScreen()

        // Update header.
        headerOptions.setTitle(header)

        // Assert updated header displayed.
        tapOnScreen()
        assertTextDisplayed(header)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderSubtitleUpdateWithNoInitialSet() = runTest {
        injectDependencies(testScheduler)

        val subtitle = "custom subtitle"
        val headerOptions =
            CallCompositeCallScreenHeaderViewData()

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(headerOptions)
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(appContext.getString(waitingForOthersToJoinString))
        tapOnScreen()

        // Update header.
        headerOptions.setSubtitle(subtitle)

        // Assert updated header displayed.
        tapOnScreen()
        assertTextDisplayed(subtitle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderTitleUpdateWithInitialSet() = runTest {
        injectDependencies(testScheduler)

        val initialTitle = "initial title"
        val headerOptions = CallCompositeCallScreenHeaderViewData()
            .setTitle(initialTitle)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(headerOptions)
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(initialTitle)
        tapOnScreen()

        // Update header.
        val header = "custom header"
        headerOptions.setTitle(header)

        // Assert updated header displayed.
        tapOnScreen()
        assertTextDisplayed(header)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderSubtitleUpdateWithInitialSet() = runTest {
        injectDependencies(testScheduler)

        val initialSubtitle = "initial subtitle"
        val headerOptions = CallCompositeCallScreenHeaderViewData()
            .setSubtitle(initialSubtitle)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderViewData(headerOptions)
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(initialSubtitle)
        tapOnScreen()

        // Update header.
        val subtitle = "custom subtitle"
        headerOptions.setSubtitle(subtitle)

        // Assert updated header displayed.
        tapOnScreen()
        assertTextDisplayed(subtitle)
    }
}
