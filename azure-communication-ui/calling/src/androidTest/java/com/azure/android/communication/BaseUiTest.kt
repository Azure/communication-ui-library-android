// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication

import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.mocking.CallEvents
import com.azure.android.communication.mocking.TestCallingSDK
import com.azure.android.communication.mocking.TestContextProvider
import com.azure.android.communication.mocking.TestVideoStreamRendererFactory
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.utilities.TestHelper
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Rule

/**
 * Basic functionality required for our UI tests: UI elements, permissions, dependency injection.
 */
internal open class BaseUiTest {
    // Commonly used UI elements.
    internal val participantCountId = R.id.azure_communication_ui_call_participant_number_text
    internal val joinCallId = R.id.azure_communication_ui_setup_join_call_button
    internal val endCallId = R.id.azure_communication_ui_call_end_call_button
    internal val participantContainerId = R.id.azure_communication_ui_call_participant_container
    internal val participantListOpenButton = R.id.azure_communication_ui_call_bottom_drawer_button
    internal val bottomDrawer = R.id.bottom_drawer_table

    @Rule
    @JvmField
    var grantPermissionRule: GrantPermissionRule

    private val basePermissionList = arrayOf(
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.WAKE_LOCK",
        "android.permission.MODIFY_AUDIO_SETTINGS",
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO"
    )

    init {
        grantPermissionRule = GrantPermissionRule.grant(*basePermissionList)
    }

    lateinit var callingSDK: TestCallingSDK
    lateinit var videoStreamRendererFactory: TestVideoStreamRendererFactory
    lateinit var callEvents: CallEvents

    // Can't be @Before due to requiring a specific test scheduler.
    fun injectDependencies(scheduler: TestCoroutineScheduler) {
        val coroutineContextProvider = TestContextProvider(UnconfinedTestDispatcher(scheduler))
        callEvents = CallEvents()
        callingSDK = TestCallingSDK(callEvents, coroutineContextProvider)
        videoStreamRendererFactory = TestVideoStreamRendererFactory(callEvents)

        TestHelper.callingSDK = callingSDK
        TestHelper.coroutineContextProvider = coroutineContextProvider
        TestHelper.videoStreamRendererFactory = videoStreamRendererFactory
    }

    @After
    fun teardown() {
        TestHelper.callingSDK = null
        TestHelper.videoStreamRendererFactory = null
        TestHelper.coroutineContextProvider = null
    }
}
