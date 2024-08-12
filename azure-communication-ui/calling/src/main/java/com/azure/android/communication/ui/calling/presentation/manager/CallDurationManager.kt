// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.annotation.SuppressLint
import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow

internal interface CallTimerAPI {
    fun startDuration(duration: Long = 0L)
    fun onStart()
    fun onStop()
    fun onReset()
}

internal class CallDurationManager : CallTimerAPI {
    private var countDownTimer: CountDownTimer
    private var timeRemaining: Long = 60000
    private var timeStart: Long = 0

    val timerTickStateFlow = MutableStateFlow("")

    init {
        countDownTimer = object : CountDownTimer(timeStart * 1000, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val secondsElapsed = (60000 - millisUntilFinished) / 1000
                timerTickStateFlow.value = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
            }

            override fun onFinish() {
            }
        }
    }

    override fun startDuration(duration: Long) {
        timeStart = duration
    }

    override fun onStart() {
        countDownTimer.start()
    }

    override fun onStop() {
        countDownTimer.cancel()
    }

    override fun onReset() {
        countDownTimer.cancel()
        timeRemaining = 60000
        timerTickStateFlow.value = "00:00"
    }
}
