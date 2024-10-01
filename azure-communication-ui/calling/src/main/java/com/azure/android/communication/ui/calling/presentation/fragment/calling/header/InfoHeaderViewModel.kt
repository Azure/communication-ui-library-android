// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

import com.azure.android.communication.ui.calling.redux.state.CallScreenInfoHeaderState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class InfoHeaderViewModel(
    val multitaskingEnabled: Boolean
) {
    private lateinit var displayFloatingHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var numberOfParticipantsFlow: MutableStateFlow<Int>

    private lateinit var timer: Timer
    private lateinit var requestCallEndCallback: () -> Unit

    private var displayedOnLaunch = false
    private lateinit var titleStateFlow: MutableStateFlow<String?>
    private lateinit var subtitleStateFlow: MutableStateFlow<String?>

    fun getTitleStateFlow(): StateFlow<String?> = titleStateFlow
    fun getSubtitleStateFlow(): StateFlow<String?> = subtitleStateFlow

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getDisplayFloatingHeaderFlow(): StateFlow<Boolean> = displayFloatingHeaderFlow

    fun getNumberOfParticipantsFlow(): StateFlow<Int> {
        return numberOfParticipantsFlow
    }

    fun update(
        numberOfRemoteParticipants: Int,
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
    ) {
        titleStateFlow.value = callScreenInfoHeaderState.title
        subtitleStateFlow.value = callScreenInfoHeaderState.subtitle
        numberOfParticipantsFlow.value = numberOfRemoteParticipants
        if (!displayedOnLaunch) {
            displayedOnLaunch = true
            switchFloatingHeader()
        }
    }

    fun updateIsOverlayDisplayed(callingStatus: CallingStatus) {
        isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus)
    }

    fun init(
        callingStatus: CallingStatus,
        numberOfRemoteParticipants: Int,
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
        requestCallEndCallback: () -> Unit,
    ) {
        timer = Timer()
        titleStateFlow = MutableStateFlow(callScreenInfoHeaderState.title)
        subtitleStateFlow = MutableStateFlow(callScreenInfoHeaderState.subtitle)
        displayFloatingHeaderFlow = MutableStateFlow(false)
        numberOfParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus))
        this.requestCallEndCallback = requestCallEndCallback
    }

    fun switchFloatingHeader() {
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
    }

    fun dismiss() {
        if (displayFloatingHeaderFlow.value) {
            displayFloatingHeaderFlow.value = false
            timer.cancel()
            return
        }
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD

    fun requestCallEnd() {
        requestCallEndCallback()
    }
}
