// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertDisplayed
import com.azure.android.communication.assertNotDisplayed
import com.azure.android.communication.assertViewText
import com.azure.android.communication.tap
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class UpperBarMessageNotificationTest : BaseUiTest() {
    @Test
    fun testNoSpeakerDevicesAvailableUpperBarMessageNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setNoSpeakerDevicesAvailable(true)

        // Check that the upper message bar notification appeared
        waitUntilDisplayed(upperMessageBarNotificationId)

        // Assert notification appears with correct text
        assertDisplayed(upperMessageBarNotificationId)
        assertDisplayed(upperMessageBarNotificationIconId)
        assertViewText(upperMessageBarNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_unable_to_locate_speaker)

        callingSDK.setNoSpeakerDevicesAvailable(false)

        // Upper Bar Message Notification not present anymore due to UFD state change
        assertNotDisplayed(upperMessageBarNotificationId)

        // Show the Upper Message Bar notification again and dismiss it using the X button
        callingSDK.setNoSpeakerDevicesAvailable(true)

        // Dismiss the notification pressing the X button
        tap(upperMessageBarNotificationDismissButtonId)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)
    }

    @Test
    fun testNoMicrophoneDevicesAvailableUpperBarMessageNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setNoMicrophoneDevicesAvailable(true)

        // Check that the upper message bar notification appeared
        waitUntilDisplayed(upperMessageBarNotificationId)

        // Assert notification appears with correct text
        assertDisplayed(upperMessageBarNotificationId)
        assertDisplayed(upperMessageBarNotificationIconId)
        assertViewText(upperMessageBarNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_unable_to_locate_microphone)

        callingSDK.setNoMicrophoneDevicesAvailable(false)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)

        // Show the Upper Message Bar notification again and dismiss it using the X button
        callingSDK.setNoMicrophoneDevicesAvailable(true)

        // Dismiss the notification pressing the X button
        tap(upperMessageBarNotificationDismissButtonId)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)
    }

    @Test
    fun testMicrophoneNotFunctioningUpperBarMessageNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setMicrophoneNotFunctioning(true)

        // Check that the upper message bar notification appeared
        waitUntilDisplayed(upperMessageBarNotificationId)

        // Assert notification appears with correct text
        assertDisplayed(upperMessageBarNotificationId)
        assertDisplayed(upperMessageBarNotificationIconId)
        assertViewText(upperMessageBarNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_microphone_not_working_as_expected)

        callingSDK.setMicrophoneNotFunctioning(false)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)

        // Show the Upper Message Bar notification again and dismiss it using the X button
        callingSDK.setMicrophoneNotFunctioning(true)

        // Dismiss the notification pressing the X button
        tap(upperMessageBarNotificationDismissButtonId)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)
    }

    @Test
    fun testSpeakerNotFunctioningUpperBarMessageNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setSpeakerNotFunctioning(true)

        // Check that the upper message bar notification appeared
        waitUntilDisplayed(upperMessageBarNotificationId)

        // Assert notification appears with correct text
        assertDisplayed(upperMessageBarNotificationId)
        assertDisplayed(upperMessageBarNotificationIconId)
        assertViewText(upperMessageBarNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_speaker_not_working_as_expected)

        callingSDK.setSpeakerNotFunctioning(false)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)

        // Show the Upper Message Bar notification again and dismiss it using the X button
        callingSDK.setSpeakerNotFunctioning(true)

        // Dismiss the notification pressing the X button
        tap(upperMessageBarNotificationDismissButtonId)

        // Upper Bar Message Notification not present anymore
        assertNotDisplayed(upperMessageBarNotificationId)
    }
}
