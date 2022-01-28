// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.header

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class InfoHeaderViewModel {
    private lateinit var displayFloatingHeaderFlow: MutableStateFlow<Boolean>

    private lateinit var numberOfParticipantsFlow: MutableStateFlow<Int>

    private lateinit var timer: Timer

    private var displayedOnLaunch = false

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

    fun init(
        numberOfRemoteParticipants: Int,
    ) {
        timer = Timer()
        displayFloatingHeaderFlow = MutableStateFlow(false)
        numberOfParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
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
}
