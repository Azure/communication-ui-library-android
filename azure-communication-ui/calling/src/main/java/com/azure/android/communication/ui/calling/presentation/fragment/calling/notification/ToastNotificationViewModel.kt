// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ToastNotificationModel
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallDiagnosticsAction
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class ToastNotificationViewModel(private val dispatch: (Action) -> Unit) {
    private var displayToastNotificationFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var toastNotificationModelMessageFlow: MutableStateFlow<ToastNotificationModel> =
        MutableStateFlow(
            ToastNotificationModel(
                0,
                0,
                null,
                null
            )
        )

    private var timer: Timer = Timer()
    private var isPersistentNotificationDisplayed: Boolean = false

    fun getDisplayToastNotificationFlow(): StateFlow<Boolean> = displayToastNotificationFlow

    fun getToastNotificationModelFlow(): StateFlow<ToastNotificationModel> = toastNotificationModelMessageFlow

    fun update(callDiagnosticsState: CallDiagnosticsState) {
        when (callDiagnosticsState.networkQualityCallDiagnostic?.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY, NetworkCallDiagnostic.NETWORK_SEND_QUALITY -> {
                if (callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.BAD ||
                    callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.POOR
                ) {
                    val toastNotificationModel = ToastNotificationModel(
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                        R.string.azure_communication_ui_calling_diagnostics_network_quality_low,
                        if (callDiagnosticsState.networkQualityCallDiagnostic?.diagnosticKind == NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY)
                            NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY else NetworkCallDiagnostic.NETWORK_SEND_QUALITY,
                        null
                    )
                    displayToastNotification(
                        toastNotificationModel,
                        false
                    )
                } else {
                    if (isPersistentNotificationDisplayed) {
                        dismiss()
                    }
                }
                isPersistentNotificationDisplayed = callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.BAD ||
                    callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.POOR
            }
            NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY -> {
                if (callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.BAD ||
                    callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.POOR
                ) {
                    val toastNotificationModel = ToastNotificationModel(
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                        R.string.azure_communication_ui_calling_diagnostics_network_reconnecting,
                        NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY,
                        null
                    )
                    displayToastNotification(
                        toastNotificationModel,
                        false
                    )
                } else {
                    if (isPersistentNotificationDisplayed) {
                        dismiss()
                    }
                }
                isPersistentNotificationDisplayed = callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.BAD ||
                    callDiagnosticsState.networkQualityCallDiagnostic.diagnosticValue == CallDiagnosticQuality.POOR
            }
            else -> {}
        }

        when (callDiagnosticsState.networkCallDiagnostic?.diagnosticKind) {
            NetworkCallDiagnostic.NETWORK_UNAVAILABLE, NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE -> {
                if (callDiagnosticsState.networkCallDiagnostic.diagnosticValue) {
                    val toastNotificationModel = ToastNotificationModel(
                        R.drawable.azure_communication_ui_calling_ic_fluent_wifi_warning_24_regular,
                        R.string.azure_communication_ui_calling_diagnostics_network_was_lost,
                        if (callDiagnosticsState.networkQualityCallDiagnostic?.diagnosticKind == NetworkCallDiagnostic.NETWORK_UNAVAILABLE)
                            NetworkCallDiagnostic.NETWORK_UNAVAILABLE else NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE,
                        null
                    )
                    displayToastNotification(toastNotificationModel)
                }
            }
            else -> {}
        }

        when (callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind) {
            MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED -> {
                if (callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    val toastNotificationModel = ToastNotificationModel(
                        R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled,
                        R.string.azure_communication_ui_calling_diagnostics_you_are_muted,
                        null,
                        MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED
                    )
                    displayToastNotification(toastNotificationModel)
                } else if (!isPersistentNotificationDisplayed) {
                    dismiss()
                }
            }
            MediaCallDiagnostic.CAMERA_START_FAILED, MediaCallDiagnostic.CAMERA_START_TIMED_OUT -> {
                if (callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    val toastNotificationModel = ToastNotificationModel(
                        R.drawable.azure_communication_ui_calling_ic_fluent_video_off_24_regular,
                        R.string.azure_communication_ui_calling_diagnostics_unable_to_start_camera,
                        null,
                        if (callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind == MediaCallDiagnostic.CAMERA_START_FAILED)
                            MediaCallDiagnostic.CAMERA_START_FAILED else MediaCallDiagnostic.CAMERA_START_TIMED_OUT,
                    )
                    displayToastNotification(toastNotificationModel)
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

    private fun displayToastNotification(toastNotificationModel: ToastNotificationModel, autoDismiss: Boolean = true) {
        if (!isPersistentNotificationDisplayed) {
            toastNotificationModelMessageFlow.value = toastNotificationModel
            displayToastNotificationFlow.value = true
            if (autoDismiss) {
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            displayToastNotificationFlow.value = false
                            if (toastNotificationModel.networkCallDiagnostic != null) {
                                if (toastNotificationModel.networkCallDiagnostic == NetworkCallDiagnostic.NETWORK_UNAVAILABLE ||
                                    toastNotificationModel.networkCallDiagnostic == NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE
                                ) {
                                    val model = NetworkCallDiagnosticModel(
                                        toastNotificationModel.networkCallDiagnostic,
                                        false
                                    )
                                    dispatch(CallDiagnosticsAction.NetworkCallDiagnosticsDismissed(model))
                                } else {
                                    val model = NetworkQualityCallDiagnosticModel(
                                        toastNotificationModel.networkCallDiagnostic,
                                        CallDiagnosticQuality.UNKNOWN
                                    )
                                    dispatch(CallDiagnosticsAction.NetworkQualityCallDiagnosticsDismissed(model))
                                }
                            }
                            if (toastNotificationModel.mediaCallDiagnostic != null) {
                                val model = MediaCallDiagnosticModel(toastNotificationModel.mediaCallDiagnostic, false)
                                dispatch(CallDiagnosticsAction.MediaCallDiagnosticsDismissed(model))
                            }
                        }
                    },
                    4000
                )
            }
        }
    }
}
