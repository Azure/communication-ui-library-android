// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.ToastNotificationModel
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ToastNotificationAction
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationKind
import com.azure.android.communication.ui.calling.redux.state.ToastNotificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Timer
import java.util.TimerTask

internal class ToastNotificationViewModel(private val dispatch: (Action) -> Unit) {
    private var toastNotificationModelMessageFlow = MutableStateFlow<ToastNotificationModel?>(null)
    private val displayPeriodMils: Long = 4 * 1000
    private var timer: Timer = Timer()
    private var isPersistentNotificationDisplayed: Boolean = false

    val toastNotificationModelFlow = toastNotificationModelMessageFlow.asStateFlow()

    fun update(toastNotificationState: ToastNotificationState) {
        when (toastNotificationState.kind) {
            ToastNotificationKind.NETWORK_RECEIVE_QUALITY ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                    isPersistent = false,
                )
            ToastNotificationKind.NETWORK_SEND_QUALITY ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                    isPersistent = false,
                )
            ToastNotificationKind.NETWORK_RECONNECTION_QUALITY ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                    isPersistent = true,
                )
            ToastNotificationKind.NETWORK_UNAVAILABLE, ToastNotificationKind.NETWORK_RELAYS_UNREACHABLE ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                    isPersistent = false,
                )

            ToastNotificationKind.SPEAKING_WHILE_MICROPHONE_IS_MUTED ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled,
                    notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_you_are_muted,
                    isPersistent = false,
                )
            ToastNotificationKind.CAMERA_START_FAILED, ToastNotificationKind.CAMERA_START_TIMED_OUT ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_video_off_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera,
                    isPersistent = false,
                )
            ToastNotificationKind.SOME_FEATURES_LOST ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_capabilities_changed_toast_features_lost,
                    isPersistent = false,
                )
            ToastNotificationKind.SOME_FEATURES_GAINED ->
                displayToastNotification(
                    notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_info_24_regular,
                    notificationMessageId = R.string.azure_communication_ui_capabilities_changed_toast_features_gained,
                    isPersistent = false,
                )
            null -> {
                dismiss()
            }
        }
    }

    fun dismiss() {
        toastNotificationModelMessageFlow.value = null
        timer.cancel()
    }

    private fun displayToastNotification(
        notificationIconId: Int,
        notificationMessageId: Int,
        isPersistent: Boolean
    ) {
        if (isPersistentNotificationDisplayed)
            return

        isPersistentNotificationDisplayed = isPersistent

        toastNotificationModelMessageFlow.value = ToastNotificationModel(
            notificationIconId = notificationIconId,
            notificationMessageId = notificationMessageId,
        )

        if (!isPersistent) {
            timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        dismiss()
                        dispatch(ToastNotificationAction.DismissNotification())
                    }
                },
                displayPeriodMils
            )
        }
    }
}
