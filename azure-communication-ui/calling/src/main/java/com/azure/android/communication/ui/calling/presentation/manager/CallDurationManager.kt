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
    fun getElapsedDuration(): Long
}

internal class CallDurationManager : CallTimerAPI {
    private var countDownTimer: CountDownTimer
    private var initialDurationInMillis: Long = 0
    private var elapsedTime: Long = 0

    val timerTickStateFlow = MutableStateFlow("")

    init {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val msElapsed = ((Long.MAX_VALUE - millisUntilFinished) + initialDurationInMillis)
                elapsedTime = msElapsed
                val secondsElapsed = msElapsed / 1000
                val hours = secondsElapsed / 3600
                val minutes = (secondsElapsed % 3600) / 60
                val seconds = secondsElapsed % 60
                val formattedTime = if (hours > 0) {
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d", minutes, seconds)
                }
                timerTickStateFlow.value = formattedTime
            }

            override fun onFinish() {
            }
        }
    }

    override fun startDuration(duration: Long) {
        if (duration == 0L) {
            return
        }
        initialDurationInMillis = duration
        elapsedTime = duration
    }

    override fun onStart() {
        countDownTimer.start()
    }

    override fun onStop() {
        countDownTimer.cancel()
    }

    override fun onReset() {
        countDownTimer.cancel()
        elapsedTime = 0
        timerTickStateFlow.value = "00:00"
    }

    override fun getElapsedDuration(): Long {
        return elapsedTime
    }
}
