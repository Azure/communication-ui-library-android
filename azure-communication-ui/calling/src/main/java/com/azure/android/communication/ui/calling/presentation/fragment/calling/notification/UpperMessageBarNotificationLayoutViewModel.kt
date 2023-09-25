// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import android.view.View
import com.azure.android.communication.calling.NetworkDiagnostics
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class UpperMessageBarNotificationModel {
    var notificationIconId : Int = 0
    var notificationMessageId : Int = 0
    var notificationView : View? = null

    fun isEmpty() : Boolean {
        return notificationIconId == 0 && notificationMessageId == 0 && notificationView == null
    }
}

internal class UpperMessageBarNotificationLayoutViewModel {
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>

    private lateinit var newUpperMessageBarNotificationFlow: MutableStateFlow<UpperMessageBarNotificationModel>
    private lateinit var dismissUpperMessageBarNotificationFlow: MutableStateFlow<UpperMessageBarNotificationModel>

    private var networkDiagnosticNotificationViews = hashMapOf<NetworkDiagnostics, UpperMessageBarNotificationModel>()
    private var mediaDiagnosticNotificationViews = hashMapOf<MediaCallDiagnostic, UpperMessageBarNotificationModel>()

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getNewUpperMessageBarNotificationFlow(): StateFlow<UpperMessageBarNotificationModel> = newUpperMessageBarNotificationFlow

    fun getDismissUpperMessageBarNotificationFlow(): StateFlow<UpperMessageBarNotificationModel> = dismissUpperMessageBarNotificationFlow

    fun update(callDiagnosticsState: CallDiagnosticsState) {

        when(callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind) {
            /*MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED -> {
                if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    var upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
                    upperMessageBarNotificationModel.notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_you_are_muted
                    upperMessageBarNotificationModel.notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled
                    mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED] = upperMessageBarNotificationModel
                    newUpperMessageBarNotificationFlow.value = upperMessageBarNotificationModel
                } else if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    dismissUpperMessageBarNotificationFlow.value = mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED]!!
                    mediaDiagnosticNotificationViews.remove(MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED)
                }
            }*/
            MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE -> {
                if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    var upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
                    upperMessageBarNotificationModel.notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_unable_to_locate_speaker
                    upperMessageBarNotificationModel.notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_speaker_mute_24_regular
                    mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE] = upperMessageBarNotificationModel
                    newUpperMessageBarNotificationFlow.value = upperMessageBarNotificationModel
                } else if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    dismissUpperMessageBarNotificationFlow.value = mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE]!!
                    mediaDiagnosticNotificationViews.remove(MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE)
                }
            }
            MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE -> {
                if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    var upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
                    upperMessageBarNotificationModel.notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_unable_to_locate_microphone
                    upperMessageBarNotificationModel.notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_mic_prohibited_24_regular
                    mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE] = upperMessageBarNotificationModel
                    newUpperMessageBarNotificationFlow.value = upperMessageBarNotificationModel
                } else if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    dismissUpperMessageBarNotificationFlow.value = mediaDiagnosticNotificationViews[MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE]!!
                    mediaDiagnosticNotificationViews.remove(MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE)
                }
            }
            MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING -> {
                if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    var upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
                    upperMessageBarNotificationModel.notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_microphone_not_working_as_expected
                    upperMessageBarNotificationModel.notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_mic_prohibited_24_regular
                    mediaDiagnosticNotificationViews[MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING] = upperMessageBarNotificationModel
                    newUpperMessageBarNotificationFlow.value = upperMessageBarNotificationModel
                } else if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    dismissUpperMessageBarNotificationFlow.value = mediaDiagnosticNotificationViews[MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING]!!
                    mediaDiagnosticNotificationViews.remove(MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING)
                }
            }
            MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING -> {
                if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    var upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
                    upperMessageBarNotificationModel.notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_speaker_not_working_as_expected
                    upperMessageBarNotificationModel.notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_speaker_mute_24_regular
                    mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING] = upperMessageBarNotificationModel
                    newUpperMessageBarNotificationFlow.value = upperMessageBarNotificationModel
                } else if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    dismissUpperMessageBarNotificationFlow.value = mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING]!!
                    mediaDiagnosticNotificationViews.remove(MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING)
                }
            }
            MediaCallDiagnostic.SPEAKER_MUTED, MediaCallDiagnostic.SPEAKER_VOLUME_ZERO -> {
                if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_MUTED] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    var upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
                    upperMessageBarNotificationModel.notificationMessageId = R.string.azure_communication_ui_calling_diagnostics_speaker_muted
                    upperMessageBarNotificationModel.notificationIconId = R.drawable.azure_communication_ui_calling_ic_fluent_speaker_mute_24_regular
                    mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_MUTED] = upperMessageBarNotificationModel
                    newUpperMessageBarNotificationFlow.value = upperMessageBarNotificationModel
                } else if (mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_MUTED] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue) {
                    dismissUpperMessageBarNotificationFlow.value = mediaDiagnosticNotificationViews[MediaCallDiagnostic.SPEAKER_MUTED]!!
                    mediaDiagnosticNotificationViews.remove(MediaCallDiagnostic.SPEAKER_MUTED)
                }
            }
            else -> {}
        }
    }

    fun init(
        callingStatus: CallingStatus
    ) {
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus))
        isOverlayDisplayedFlow = MutableStateFlow(false)
        newUpperMessageBarNotificationFlow = MutableStateFlow(UpperMessageBarNotificationModel())
        dismissUpperMessageBarNotificationFlow = MutableStateFlow(UpperMessageBarNotificationModel())
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD
}
