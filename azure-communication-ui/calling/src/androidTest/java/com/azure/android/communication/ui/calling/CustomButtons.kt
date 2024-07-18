// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertTextDisplayed
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.tapWithTextWhenDisplayed
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class CustomButtons : BaseUiTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomButtonsDisplayed() = runTest {
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

        var button1Clicked = false
        var button2Clicked = false

        val controlBarOptions = CallCompositeCallScreenControlBarOptions()
            .addCustomButton(
                CallCompositeCustomButtonOptions(
                    R.drawable.azure_communication_ui_calling_ic_fluent_speaker_bluetooth_24_regular_primary,
                    "Custom button 1"
                ){
                    button1Clicked = true
                }
            )
            .addCustomButton(
                CallCompositeCustomButtonOptions(
                    R.drawable.azure_communication_ui_calling_ic_fluent_speaker_bluetooth_24_regular_primary,
                    "Custom button 2"
                ){
                    button2Clicked = true
                }
            )

        val callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(controlBarOptions)
        val localOptions = CallCompositeLocalOptions().setCallScreenOptions(callScreenOptions)

        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            localOptions
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        tapWhenDisplayed(moreOptionsId)

        assertTextDisplayed("Custom button 1")
        assertTextDisplayed("Custom button 2")

        tapWithTextWhenDisplayed("Custom button 1")
        tapWhenDisplayed(moreOptionsId)
        tapWithTextWhenDisplayed("Custom button 2")

        assert(button1Clicked)
        assert(button2Clicked)
    }
}