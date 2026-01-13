// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication

import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.mocking.CallEvents
import com.azure.android.communication.mocking.TestCallingSDK
import com.azure.android.communication.mocking.TestContextProvider
import com.azure.android.communication.mocking.TestVideoStreamRendererFactory
import com.azure.android.communication.ui.calling.implementation.R
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
    internal val moreOptionsId = R.id.azure_communication_ui_call_control_bar_more
    internal val participantContainerId = R.id.azure_communication_ui_call_participant_container
    internal val participantListOpenButton = R.id.azure_communication_ui_call_bottom_drawer_button
    internal val bottomDrawer = R.id.bottom_drawer_table
    internal val lobbyHeaderId = R.id.azure_communication_ui_calling_lobby_header
    internal val lobbyHeaderText = R.id.azure_communication_ui_calling_lobby_header_text
    internal val lobbyHeaderCloseButton = R.id.azure_communication_ui_calling_lobby_close_button
    internal val lobbyHeaderOpenParticipantListButton = R.id.azure_communication_ui_calling_lobby_open_list_button
    internal val lobbyErrorHeaderId = R.id.azure_communication_ui_calling_lobby_error_header
    internal val lobbyErrorHeaderText = R.id.azure_communication_ui_lobby_header_error_text
    internal val lobbyErrorHeaderCloseButton = R.id.azure_communication_ui_calling_lobby_error_close_button
    internal val toastNotificationId = R.id.azure_communication_ui_calling_toast_notification
    internal val toastNotificationIconId = R.id.azure_communication_ui_calling_toast_notification_icon
    internal val toastNotificationMessageId = R.id.azure_communication_ui_calling_toast_notification_message
    internal val upperMessageBarNotificationId = R.id.azure_communication_ui_calling_upper_message_bar_notification
    internal val upperMessageBarNotificationIconId = R.id.azure_communication_ui_calling_upper_message_bar_notification_icon
    internal val upperMessageBarNotificationMessageId = R.id.azure_communication_ui_calling_upper_message_bar_notification_message
    internal val upperMessageBarNotificationDismissButtonId = R.id.azure_communication_ui_calling_upper_message_bar_notification_dismiss_button
    internal val setupCameraButtonId = R.id.azure_communication_ui_setup_camera_button
    internal val callCameraButtonId = R.id.azure_communication_ui_call_switch_camera_button
    internal val audioDeviceSelectionButtonId = R.id.azure_communication_ui_call_audio_device_button
    internal val androidAudioDevice = R.string.azure_communication_ui_calling_audio_device_drawer_android
    internal val startCallString = R.string.azure_communication_ui_calling_setup_view_start_call
    internal val callingString = R.string.azure_communication_ui_calling_call_view_calling
    internal val audioTextView = R.id.azure_communication_ui_participant_audio_view_display_name
    /* <CALL_START_TIME> */
    internal val callDurationTextView = R.id.azure_communication_ui_call_header_duration
    /* </CALL_START_TIME> */

    // Support Form
    internal val sendButtonId = R.id.azure_communication_ui_send_button
    internal val cancelButtonId = R.id.azure_communication_ui_cancel_button
    internal val userMessageEditTextId = R.id.azure_communication_ui_user_message_edit_text
    internal val showSupportFormTextId = R.string.azure_communication_ui_calling_report_issue_title
    internal val groupCallTestGUID = "9d3ee939-cd87-4ec1-b076-94b2b7382523"

    // Captions
    internal val captionsStartToggleId = R.id.azure_communication_ui_calling_bottom_drawer_toggle_button
    internal val liveCaptionsStringId = R.string.azure_communication_ui_calling_live_captions_title
    internal val captionsTextViewId = R.id.azure_communication_ui_calling_captions_linear_layout
    internal val captionsStartInProgressUI = R.id.azure_communication_ui_calling_captions_starting_layout
    internal val captionsStartInProgressUIText = R.string.azure_communication_ui_calling_starting_captions
    internal val captionsFailedToStartId = R.string.azure_communication_ui_calling_error_captions_failed_to_start
    internal val captionsFailedToStopId = R.string.azure_communication_ui_calling_error_captions_failed_to_stop
    internal val captionsFailedToSetSpokenLanguageId = R.string.azure_communication_ui_calling_error_captions_failed_to_set_spoken_language
    internal val captionsFailedToSetCaptionLanguageId = R.string.azure_communication_ui_calling_error_captions_failed_to_set_caption_language
    internal val captionsSpokenLanguageId = R.string.azure_communication_ui_calling_captions_spoken_language_title
    internal val captionsCaptionLanguageId = R.string.azure_communication_ui_calling_captions_caption_language_title
    internal val waitingForOthersToJoinString = R.string.azure_communication_ui_calling_view_info_header_waiting_for_others_to_join

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
