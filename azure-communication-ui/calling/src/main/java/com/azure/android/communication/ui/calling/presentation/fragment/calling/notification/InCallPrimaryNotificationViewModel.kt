// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class InCallPrimaryNotificationViewModel {
    private lateinit var displayInCallNotificationFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var inCallNotificationMessageFlow: MutableStateFlow<String>
    private lateinit var inCallNotificationIconFlow: MutableStateFlow<Int>

    private var _shouldShowIsSpeakingWhileMutedMessageStateFlow = MutableStateFlow(false)
    var shouldShowIsSpeakingWhileMutedMessageStateFlow = _shouldShowIsSpeakingWhileMutedMessageStateFlow

    private lateinit var timer: Timer

    private var displayedOnLaunch = false

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getDisplayInCallNotificationFlow(): StateFlow<Boolean> = displayInCallNotificationFlow

    fun getInCallNotificationMessageFlow(): StateFlow<String> = inCallNotificationMessageFlow

    fun getInCallNotificationIconFlow(): StateFlow<Int> = inCallNotificationIconFlow

    fun update(callDiagnosticsState: CallDiagnosticsState) {
        if (!displayedOnLaunch) {
            displayedOnLaunch = true
            //switchFloatingHeader()
        }
        /*shouldShowIsSpeakingWhileMutedMessageStateFlow.value = callDiagnosticsState.isSpeakingWhileMicrophoneIsMuted
        _shouldShowIsSpeakingWhileMutedMessageStateFlow.value = callDiagnosticsState.isSpeakingWhileMicrophoneIsMuted
        displayInCallNotificationFlow.value = callDiagnosticsState.isSpeakingWhileMicrophoneIsMuted

        inCallNotificationMessageFlow.value = "You're muted"
        inCallNotificationIconFlow.value = R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled_composite_button_filled_grey;

         */

        if (callDiagnosticsState.mediaCallDiagnostic?.diagnosticKind == MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED) {
            shouldShowIsSpeakingWhileMutedMessageStateFlow.value = callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
            _shouldShowIsSpeakingWhileMutedMessageStateFlow.value = callDiagnosticsState.mediaCallDiagnostic.diagnosticValue
            displayInCallNotificationFlow.value = callDiagnosticsState.mediaCallDiagnostic.diagnosticValue

            inCallNotificationMessageFlow.value = "You're muted"
            inCallNotificationIconFlow.value = R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled_composite_button_filled_grey;
        }
    }

    fun updateIsOverlayDisplayed(callingStatus: CallingStatus) {
        isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus)
    }

    fun init(
        callingStatus: CallingStatus,
        numberOfRemoteParticipants: Int
    ) {
        timer = Timer()
        displayInCallNotificationFlow = MutableStateFlow(false)
        inCallNotificationMessageFlow = MutableStateFlow("")
        inCallNotificationIconFlow = MutableStateFlow(0)
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus))
    }

    /*fun switchFloatingHeader() {
        if (displayFloatingHeaderFlow.value) {
            displayFloatingHeaderFlow.value = false
            timer.cancel()

            return
        }
        displayFloatingHeaderFlow.value = true
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    displayFloatingHeaderFlow.value = false
                }
            },
            3000
        )
    }*/

    fun dismiss() {
        if (displayInCallNotificationFlow.value) {
            displayInCallNotificationFlow.value = false
            //timer.cancel()
            return
        }
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD
}
