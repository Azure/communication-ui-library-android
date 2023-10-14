// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertDisplayed
import com.azure.android.communication.assertViewText
import com.azure.android.communication.ui.R
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test


internal class ToastNotificationTest : BaseUiTest() {
    @Test
    fun testShowLowNetworkReceiveQualityToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setLowNetworkRecieveQuality(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_quality_low)

        callingSDK.setLowNetworkRecieveQuality(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_quality_low)
    }

    @Test
    fun testShowLowNetworkSendQualityToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setLowNetworkSendQuality(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_quality_low)

        callingSDK.setLowNetworkSendQuality(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_quality_low)
    }

    @Test
    fun testShowLowNetworkReconnectionQualityToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setLowNetworkReconnectionQuality(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_reconnecting)

        // Stop speaking while muted
        callingSDK.setLowNetworkReconnectionQuality(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_reconnecting)
    }

    @Test
    fun testShowNetworkUnavailableToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setNetworkUnavailable(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_was_lost)

        // Stop speaking while muted
        callingSDK.setNetworkUnavailable(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_was_lost)
    }

    @Test
    fun testShowNetworkRelaysUnreachableToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setNetworkRelaysUnreachable(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_was_lost)

        // Stop speaking while muted
        callingSDK.setNetworkRelaysUnreachable(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_network_was_lost)
    }

    @Test
    fun testShowSpeakingWhileMutedToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setSpeakingWhileMuted(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_you_are_muted)

        // Stop speaking while muted
        callingSDK.setSpeakingWhileMuted(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_you_are_muted)
    }

    @Test
    fun testShowCameraStartFailedToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setCameraStartFailed(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera)

        // Stop speaking while muted
        callingSDK.setCameraStartFailed(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera)
    }

    @Test
    fun testShowCameraStartTimedOutToastNotification() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.setCameraStartTimedOut(true)

        // Check that the toast notification appeared
        waitUntilDisplayed(toastNotificationId)

        // Assert toast notification appears with correct text
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera)

        // Stop speaking while muted
        callingSDK.setCameraStartTimedOut(false)

        // Assert toast notification is still shown even after UFD is set to false
        assertDisplayed(toastNotificationId)
        assertDisplayed(toastNotificationIconId)
        assertViewText(toastNotificationMessageId, R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera)
    }
}
