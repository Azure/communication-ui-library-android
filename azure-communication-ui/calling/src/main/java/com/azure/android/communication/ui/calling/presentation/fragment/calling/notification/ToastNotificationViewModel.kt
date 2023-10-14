// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.ToastNotificationModel
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class ToastNotificationViewModel {
    private var displayToastNotificationFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var toastNotificationModelMessageFlow: MutableStateFlow<ToastNotificationModel> =
        MutableStateFlow(ToastNotificationModel())

    private var timer: Timer = Timer()

    fun getDisplayToastNotificationFlow(): StateFlow<Boolean> = displayToastNotificationFlow

    fun getToastNotificationModelFlow(): MutableStateFlow<ToastNotificationModel> = toastNotificationModelMessageFlow

    fun update(callDiagnosticsState: CallDiagnosticsState) {
        when (callDiagnosticsState.networkQualityCallDiagnostic?.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY, NetworkCallDiagnostic.NETWORK_SEND_QUALITY -> {
                if (callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.BAD ||
                    callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.POOR
                ) {
                    displayToastNotification(
                        R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular
                    )
                }
            }
            NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY -> {
                if (callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.BAD ||
                    callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.POOR
                ) {
                    displayToastNotification(
                        R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular
                    )
                }
            }
            else -> {}
        }

        when (callDiagnosticsState.networkCallDiagnostic?.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_UNAVAILABLE, NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE -> {
                if (callDiagnosticsState.networkCallDiagnostic.diagnosticValue) {
                    displayToastNotification(
                        R.string.azure_communication_ui_calling_diagnostics_network_was_lost,
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular
                    )
                }
            }
            else -> {}
        }

        when (callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind) {
            MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED -> {
                if (callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    displayToastNotification(
                        R.string.azure_communication_ui_calling_diagnostics_you_are_muted,
                        R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled
                    )
                }
            }
            MediaCallDiagnostic.CAMERA_START_FAILED, MediaCallDiagnostic.CAMERA_START_TIMED_OUT -> {
                if (callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    displayToastNotification(
                        R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera,
                        R.drawable.azure_communication_ui_calling_ic_fluent_video_off_24_regular
                    )
                }
            }
            else -> {}
        }
    }

    fun dismiss() {
        if (displayToastNotificationFlow.value) {
            displayToastNotificationFlow.value = false
            timer.cancel()
            return
        }
    }

    private fun displayToastNotification(notificationMessageId: Int, notificationIconId: Int) {
        displayToastNotificationFlow.value = true
        val toastNotificationModel = ToastNotificationModel()
        toastNotificationModel.notificationIconId = notificationIconId
        toastNotificationModel.notificationMessageId = notificationMessageId
        toastNotificationModelMessageFlow.value = toastNotificationModel

        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    displayToastNotificationFlow.value = false
                }
            },
            4000
        )
    }
}
