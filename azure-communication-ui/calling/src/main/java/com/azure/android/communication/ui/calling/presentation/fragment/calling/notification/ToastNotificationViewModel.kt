// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.calling.DiagnosticQuality
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class ToastNotificationModel {
    var notificationIconId : Int = 0
    var notificationMessageId : Int = 0

    fun isEmpty() : Boolean {
        return notificationIconId == 0 && notificationMessageId == 0
    }
}

internal class ToastNotificationViewModel {
    private lateinit var displayToastNotificationFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>

    private lateinit var toastNotificationModelMessageFlow: MutableStateFlow<ToastNotificationModel>

    private lateinit var timer: Timer

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getDisplayToastNotificationFlow(): StateFlow<Boolean> = displayToastNotificationFlow

    fun getToastNotificationModelFlow(): StateFlow<ToastNotificationModel> = toastNotificationModelMessageFlow

    fun update(callDiagnosticsState: CallDiagnosticsState) {
        when(callDiagnosticsState.networkQualityCallDiagnostic?.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY, NetworkCallDiagnostic.NETWORK_SEND_QUALITY -> {
                if (callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == DiagnosticQuality.BAD
                    || callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == DiagnosticQuality.POOR) {
                    displayToastNotification(R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular)
                }
            }
            NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY -> {
                if (callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == DiagnosticQuality.BAD
                    || callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == DiagnosticQuality.POOR) {
                        displayToastNotification(R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                            R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular)
                }
            }
            else -> {}
        }

        when(callDiagnosticsState.networkCallDiagnostic?.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_UNAVAILABLE, NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE -> {
                if (callDiagnosticsState.networkCallDiagnostic.diagnosticValue) {
                    displayToastNotification(R.string.azure_communication_ui_calling_diagnostics_network_was_lost,
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular)
                }
            }
            else -> {}
        }

        when(callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind) {
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

    fun updateIsOverlayDisplayed(callingStatus: CallingStatus) {
        isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus)
    }

    fun init(
        callingStatus: CallingStatus
    ) {
        timer = Timer()
        displayToastNotificationFlow = MutableStateFlow(false)
        toastNotificationModelMessageFlow = MutableStateFlow(ToastNotificationModel())
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus))
    }

    fun dismiss() {
        if (displayToastNotificationFlow.value) {
            displayToastNotificationFlow.value = false
            timer.cancel()
            return
        }
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD

    private fun displayToastNotification(notificationMessageId: Int, notificationIconId: Int) {
        displayToastNotificationFlow.value = true
        val toastNotificationModel = ToastNotificationModel()
        toastNotificationModel.notificationIconId = notificationIconId
        toastNotificationModel.notificationMessageId = notificationMessageId
        toastNotificationModelMessageFlow.value = toastNotificationModel

        timer = Timer()
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
