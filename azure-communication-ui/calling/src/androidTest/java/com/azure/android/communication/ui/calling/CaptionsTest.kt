// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertDisplayed
import com.azure.android.communication.assertNotDisplayed
import com.azure.android.communication.assertTextDisplayed
import com.azure.android.communication.assertTextNotDisplayed
import com.azure.android.communication.calling.CallingCommunicationErrors
import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapOnScreen
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.tapWithTextWhenDisplayed
import com.azure.android.communication.tapWithTextWhenDisplayedById
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsOptions
import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.waitUntilDisplayed
import com.azure.android.communication.waitUntilTextDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Date

internal class CaptionsTest : BaseUiTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionsUIDisplayedOnStart() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        callingSDK.startCaptionsCompletableFuture.complete(null)
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionsStartingDisplayedOnStart() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions waiting UI should be displayed.
        waitUntilDisplayed(captionsStartInProgressUI)
        assertDisplayed(captionsStartInProgressUI)

        // on start success should hide the waiting UI.
        callingSDK.startCaptionsCompletableFuture.complete(null)

        // instead of sleep we can tap on screen to dismiss the waiting UI.
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapOnScreen()

        // Captions should be displayed without the waiting UI.
        assertTextNotDisplayed(captionsStartInProgressUIText)
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionsStartFailedError() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val callComposite = CallCompositeBuilder()
            .applicationContext(appContext)
            .credential(communicationTokenCredential)
            .build()

        var errorEventReceived = false

        callComposite.addOnErrorEventHandler {
            if (it.errorCode == CallCompositeErrorCode.CAPTIONS_START_FAILED_SPOKEN_LANGUAGE_NOT_SUPPORTED) {
                errorEventReceived = true
            }
        }

        callComposite.launchTest(appContext, CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"), null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions waiting UI should be displayed.
        waitUntilDisplayed(captionsStartInProgressUI)
        assertDisplayed(captionsStartInProgressUI)

        // start captions failed
        val error = Error(CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED))
        callingSDK.startCaptionsCompletableFuture.completeExceptionally(error)

        // failure alert should be displayed.
        waitUntilTextDisplayed(captionsFailedToStartId)

        // instead of sleep we can tap on screen to dismiss the waiting UI.
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapOnScreen()

        // Captions should be displayed without the waiting UI.
        assertTextNotDisplayed(captionsStartInProgressUIText)
        assertNotDisplayed(captionsTextViewId)
        assert(errorEventReceived)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionsStopHideCaptionsUI() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions waiting UI should be displayed.
        waitUntilDisplayed(captionsStartInProgressUI)
        assertDisplayed(captionsStartInProgressUI)

        // on start success should hide the waiting UI.
        callingSDK.startCaptionsCompletableFuture.complete(null)

        // Stop captions.
        callingSDK.stopCaptionsCompletableFuture.complete(null)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // UI should not be hidden.
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapOnScreen()
        assertNotDisplayed(captionsTextViewId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionsStopFailureError() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions waiting UI should be displayed.
        waitUntilDisplayed(captionsStartInProgressUI)
        assertDisplayed(captionsStartInProgressUI)

        // on start success should hide the waiting UI.
        callingSDK.startCaptionsCompletableFuture.complete(null)

        // Stop captions.
        val error = Error(CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_FAILED_TO_STOP))
        callingSDK.stopCaptionsCompletableFuture.completeExceptionally(error)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // instead of sleep we can tap on screen to dismiss the waiting UI.
        waitUntilTextDisplayed(captionsFailedToStopId)

        // UI should not be hidden.
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapOnScreen()
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSpokenLanguageChange() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        callingSDK.startCaptionsCompletableFuture.complete(null)
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)

        // Change spoken language.
        callingSDK.addSpokenLanguages()
        callingSDK.setActiveSpokenLanguage("en-US")
        callingSDK.setSpokenLanguageCompletableFuture.complete(null)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayed(appContext.getString(captionsSpokenLanguageId))
        callingSDK.setActiveSpokenLanguage("en-GB")
        tapWithTextWhenDisplayed("English (United Kingdom)")
        tapOnScreen()

        // verify spoken language is changed
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        assertTextDisplayed("English (United Kingdom)")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSpokenLanguageError() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val callComposite = CallCompositeBuilder()
            .applicationContext(appContext)
            .credential(communicationTokenCredential)
            .build()

        var errorEventReceived = false

        callComposite.addOnErrorEventHandler {
            if (it.errorCode == CallCompositeErrorCode.CAPTIONS_NOT_ACTIVE) {
                errorEventReceived = true
            }
        }

        callComposite.launchTest(appContext, CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"), null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        callingSDK.startCaptionsCompletableFuture.complete(null)
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)

        // Change spoken language.
        callingSDK.addSpokenLanguages()
        callingSDK.setActiveSpokenLanguage("en-US")
        val error = Error(CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE))
        callingSDK.setSpokenLanguageCompletableFuture.completeExceptionally(error)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayed(appContext.getString(captionsSpokenLanguageId))
        callingSDK.setActiveSpokenLanguage("en-GB")
        tapWithTextWhenDisplayed("English (United Kingdom)")
        tapOnScreen()

        // failure alert should be displayed.
        waitUntilTextDisplayed(captionsFailedToSetSpokenLanguageId)
        assert(errorEventReceived)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionLanguageChange() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        callingSDK.startCaptionsCompletableFuture.complete(null)
        callingSDK.setCaptionsTranslationSupported(true)
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)

        // Change spoken language.
        callingSDK.addCaptionLanguages()
        callingSDK.setActiveCaptionLanguage("en-US")
        callingSDK.setCaptionLanguageCompletableFuture.complete(null)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayed(appContext.getString(captionsCaptionLanguageId))
        callingSDK.setActiveCaptionLanguage("en-GB")
        tapWithTextWhenDisplayed("English (United Kingdom)")
        tapOnScreen()

        // verify spoken language is changed
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        assertTextDisplayed("English (United Kingdom)")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionLanguageError() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val callComposite = CallCompositeBuilder()
            .applicationContext(appContext)
            .credential(communicationTokenCredential)
            .build()

        var errorEventReceived = false

        callComposite.addOnErrorEventHandler {
            if (it.errorCode == CallCompositeErrorCode.CAPTIONS_NOT_ACTIVE) {
                errorEventReceived = true
            }
        }

        callComposite.launchTest(appContext, CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"), null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        callingSDK.startCaptionsCompletableFuture.complete(null)
        callingSDK.setCaptionsTranslationSupported(true)
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)

        // Change spoken language.
        callingSDK.addCaptionLanguages()
        callingSDK.setActiveCaptionLanguage("en-US")
        val error = Error(CallingCommunicationException(CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE))
        callingSDK.setCaptionLanguageCompletableFuture.completeExceptionally(error)
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        tapWithTextWhenDisplayed(appContext.getString(captionsCaptionLanguageId))
        callingSDK.setActiveCaptionLanguage("en-GB")
        tapWithTextWhenDisplayed("English (United Kingdom)")
        tapOnScreen()

        // failure alert should be displayed.
        waitUntilTextDisplayed(captionsFailedToSetCaptionLanguageId)
        assert(errorEventReceived)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCaptionsUITextOnStart() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = joinTeamsCall()
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed(appContext.getString(liveCaptionsStringId))
        callingSDK.startCaptionsCompletableFuture.complete(null)
        tapWithTextWhenDisplayedById(captionsStartToggleId)
        tapOnScreen()

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)

        // notify data
        callingSDK.setCaptionsReceived(
            CallCompositeCaptionsData(
                CaptionsResultType.FINAL,
                "test",
                "test",
                "test",
                "spoken text test",
                Date()
            )
        )

        // Captions should be displayed.
        assertTextDisplayed("spoken text test")

        callingSDK.setActiveCaptionLanguage("en-US")

        // notify data
        callingSDK.setCaptionsReceived(
            CallCompositeCaptionsData(
                CaptionsResultType.FINAL,
                "test",
                "test",
                "test",
                "spoken text test",
                Date(),
                "en-US",
                "caption text",
            )
        )

        // Captions should be displayed.
        assertTextDisplayed("caption text")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAutoStartCaptions() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val callComposite = CallCompositeBuilder()
            .applicationContext(appContext)
            .credential(communicationTokenCredential)
            .build()

        val captionsOption = CallCompositeCaptionsOptions().setCaptionsOn(true)
        val localOptions = CallCompositeLocalOptions().setCaptionsOptions(captionsOption)
        callComposite.launchTest(appContext, CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"), localOptions)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.startCaptionsCompletableFuture.complete(null)

        // Captions should be displayed.
        waitUntilDisplayed(captionsTextViewId)
        assertDisplayed(captionsTextViewId)
    }

    private fun joinTeamsCall(): Context {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder().build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
                communicationTokenCredential,
                "test"
            )

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        return appContext
    }
}
