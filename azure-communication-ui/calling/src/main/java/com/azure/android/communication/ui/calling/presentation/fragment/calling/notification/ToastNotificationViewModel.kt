// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ToastNotificationAction
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationKind
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

internal class ToastNotificationViewModel(private val dispatch: (Action) -> Unit) {
    private val displayedNotifications = mutableListOf<ToastNotificationModel>()
    private val mutableDisplayedNotificationsFlow = MutableStateFlow<List<ToastNotificationModel>>(listOf())
    private val displayPeriodSeconds: Long = 4

    val displayedNotificationsFlow = mutableDisplayedNotificationsFlow.asStateFlow()

    fun init(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            startCleanupProcessing(coroutineScope)
        }
    }

    fun update(toastNotificationState: ToastNotificationState) {
        // Remove persistent notifications that are not in the new state.
        // Non-persistent notifications are removed after a certain time by cleanup process.
        displayedNotifications.removeIf { it.kind !in toastNotificationState.kinds && it.isPersistent }

        // Add new notifications
        addNewNotifications(toastNotificationState)

        notifyView()
    }

    private fun notifyView() {
        mutableDisplayedNotificationsFlow.value = displayedNotifications.toList()
    }

    private fun addNewNotifications(toastNotificationState: ToastNotificationState) {
        toastNotificationState.kinds
            .filter { kind -> kind !in this.displayedNotifications.map { it.kind } }
            .forEach { kind ->

                when (kind) {
                    ToastNotificationKind.NETWORK_RECEIVE_QUALITY ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.NETWORK_SEND_QUALITY ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.NETWORK_RECONNECTION_QUALITY ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                            isPersistent = true,
                            kind,
                        )

                    ToastNotificationKind.NETWORK_UNAVAILABLE, ToastNotificationKind.NETWORK_RELAYS_UNREACHABLE ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.SPEAKING_WHILE_MICROPHONE_IS_MUTED ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled,
                            notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_you_are_muted,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.CAMERA_START_FAILED, ToastNotificationKind.CAMERA_START_TIMED_OUT ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_video_off_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.SOME_FEATURES_LOST ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_view_capabilities_changed_toast_features_lost,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.SOME_FEATURES_GAINED ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_view_capabilities_changed_toast_features_gained,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.CAPTIONS_FAILED_TO_START ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_error_captions_failed_to_start,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.CAPTIONS_FAILED_TO_STOP ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_error_captions_failed_to_stop,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_error_captions_failed_to_set_spoken_language,
                            isPersistent = false,
                            kind,
                        )

                    ToastNotificationKind.CAPTIONS_FAILED_TO_SET_CAPTION_LANGUAGE ->
                        displayToastNotification(
                            notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                            notificationMessageId = R.string.azure_communication_ui_calling_error_captions_failed_to_set_caption_language,
                            isPersistent = false,
                            kind,
                        )
                }
            }
    }

    private fun displayToastNotification(
        notificationIconId: Int,
        notificationMessageId: Int,
        isPersistent: Boolean,
        kind: ToastNotificationKind,
    ) {
        displayedNotifications.add(
            ToastNotificationModel(
                notificationIconId = notificationIconId,
                notificationMessageId = notificationMessageId,
                notificationDisplayedAt = Date(),
                kind = kind,
                isPersistent = isPersistent,
            )
        )
    }

    private suspend fun startCleanupProcessing(
        coroutineScope: CoroutineScope
    ) {
        while (coroutineScope.isActive) {
            displayedNotifications
                .filter {
                    !it.isPersistent &&
                        it.notificationDisplayedAt < Date.from(
                        Instant.now().minusSeconds(displayPeriodSeconds)
                    )
                }
                .onEach {
                    dispatch(ToastNotificationAction.DismissNotification(it.kind))
                    displayedNotifications.remove(it)
                }
                .also {
                    if (it.isNotEmpty()) {
                        notifyView()
                    }
                }

            delay(timeMillis = 1000)
        }
    }
}

internal data class ToastNotificationModel(
    val notificationIconId: Int,
    val notificationMessageId: Int,
    val notificationDisplayedAt: Date,
    val kind: ToastNotificationKind,
    val isPersistent: Boolean,
)
