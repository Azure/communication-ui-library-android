// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.UpperMessageBarNotificationModel
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class UpperMessageBarNotificationLayoutViewModel(private val dispatch: (Action) -> Unit) {
    private var newUpperMessageBarNotificationFlow: MutableStateFlow<UpperMessageBarNotificationViewModel> =
        MutableStateFlow(
            UpperMessageBarNotificationViewModel(
                dispatch,
                UpperMessageBarNotificationModel(
                    0,
                    0,
                    null,
                ),
            ),
        )

    private var mediaDiagnosticNotificationViewModels = hashMapOf<MediaCallDiagnostic, UpperMessageBarNotificationViewModel>()

    fun getNewUpperMessageBarNotificationFlow(): StateFlow<UpperMessageBarNotificationViewModel> = newUpperMessageBarNotificationFlow

    fun update(callDiagnosticsState: CallDiagnosticsState) {
        when (callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind) {
            MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE -> {
                if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    var upperMessageBarNotificationModel =
                        UpperMessageBarNotificationModel(
                            R.drawable.azure_communication_ui_calling_ic_fluent_speaker_mute_24_regular,
                            R.string.azure_communication_ui_calling_diagnostics_unable_to_locate_speaker,
                            MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE,
                        )
                    addNewNotification(upperMessageBarNotificationModel)
                } else if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    dismissNotification(MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE)
                }
            }
            MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE -> {
                if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    var upperMessageBarNotificationModel =
                        UpperMessageBarNotificationModel(
                            R.drawable.azure_communication_ui_calling_ic_fluent_mic_prohibited_24_regular,
                            R.string.azure_communication_ui_calling_diagnostics_unable_to_locate_microphone,
                            MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE,
                        )
                    addNewNotification(upperMessageBarNotificationModel)
                } else if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    dismissNotification(MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE)
                }
            }
            MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING -> {
                if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    var upperMessageBarNotificationModel =
                        UpperMessageBarNotificationModel(
                            R.drawable.azure_communication_ui_calling_ic_fluent_mic_prohibited_24_regular,
                            R.string.azure_communication_ui_calling_diagnostics_microphone_not_working_as_expected,
                            MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING,
                        )
                    addNewNotification(upperMessageBarNotificationModel)
                } else if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    dismissNotification(MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING)
                }
            }
            MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING -> {
                if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING] == null &&
                    callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    var upperMessageBarNotificationModel =
                        UpperMessageBarNotificationModel(
                            R.drawable.azure_communication_ui_calling_ic_fluent_speaker_mute_24_regular,
                            R.string.azure_communication_ui_calling_diagnostics_speaker_not_working_as_expected,
                            MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING,
                        )
                    addNewNotification(upperMessageBarNotificationModel)
                } else if (mediaDiagnosticNotificationViewModels[MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING] != null &&
                    !callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
                ) {
                    dismissNotification(MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING)
                }
            }
            else -> {}
        }
    }

    private fun addNewNotification(upperMessageBarNotificationModel: UpperMessageBarNotificationModel) {
        upperMessageBarNotificationModel.mediaCallDiagnostic?.let {
            val upperMessageNotificationViewModel =
                UpperMessageBarNotificationViewModel(
                    dispatch,
                    upperMessageBarNotificationModel,
                )
            mediaDiagnosticNotificationViewModels[upperMessageBarNotificationModel.mediaCallDiagnostic] =
                upperMessageNotificationViewModel
            newUpperMessageBarNotificationFlow.value = upperMessageNotificationViewModel
        }
    }

    private fun dismissNotification(mediaCallDiagnostic: MediaCallDiagnostic) {
        val upperMessageBarNotificationViewModel = mediaDiagnosticNotificationViewModels[mediaCallDiagnostic]
        upperMessageBarNotificationViewModel?.dismissNotification()
        mediaDiagnosticNotificationViewModels.remove(mediaCallDiagnostic)
    }
}
