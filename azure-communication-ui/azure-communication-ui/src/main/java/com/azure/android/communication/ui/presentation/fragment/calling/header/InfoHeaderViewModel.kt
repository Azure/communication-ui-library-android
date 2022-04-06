// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.header

import com.azure.android.communication.ui.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class InfoHeaderViewModel {
    private lateinit var displayFloatingHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var isLobbyOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var numberOfParticipantsFlow: MutableStateFlow<Int>

    private lateinit var timer: Timer

    private var displayedOnLaunch = false

    fun getIsLobbyOverlayDisplayedFlow(): StateFlow<Boolean> = isLobbyOverlayDisplayedFlow

    fun getDisplayFloatingHeaderFlow(): StateFlow<Boolean> = displayFloatingHeaderFlow

    fun getNumberOfParticipantsFlow(): StateFlow<Int> {
        return numberOfParticipantsFlow
    }

    fun update(
        numberOfRemoteParticipants: Int,
    ) {
        numberOfParticipantsFlow.value = numberOfRemoteParticipants
        if (!displayedOnLaunch) {
            displayedOnLaunch = true
            switchFloatingHeader()
        }
    }

    fun updateIsLobbyOverlayDisplayed(callingStatus: CallingStatus) {
        isLobbyOverlayDisplayedFlow.value = isLobbyOverlayDisplayed(callingStatus)
    }

    fun init(
        callingStatus: CallingStatus,
        numberOfRemoteParticipants: Int
    ) {
        timer = Timer()
        displayFloatingHeaderFlow = MutableStateFlow(false)
        numberOfParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
        isLobbyOverlayDisplayedFlow = MutableStateFlow(isLobbyOverlayDisplayed(callingStatus))
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

    private fun isLobbyOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY
}
