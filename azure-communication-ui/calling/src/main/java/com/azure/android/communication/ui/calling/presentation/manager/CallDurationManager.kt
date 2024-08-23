// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.presentation.manager

import android.annotation.SuppressLint
import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow

internal interface CallTimer {
    fun onStart()
    fun onStop()
    fun onReset()
    fun getElapsedDuration(): Long
}

internal class CallDurationManager(private var initialElapsedDurationInMillis: Long = 0) : CallTimer {
    private var countDownTimer: CountDownTimer
    private var elapsedTime: Long = initialElapsedDurationInMillis
    private var msStopped: Long = 0
    private var msUntilFinished: Long = 0

    val timerTickStateFlow = MutableStateFlow("")

    init {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                msUntilFinished = millisUntilFinished
                val msElapsed = ((Long.MAX_VALUE - millisUntilFinished) + initialElapsedDurationInMillis + msStopped)
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

    override fun onStart() {
        countDownTimer.start()
    }

    override fun onStop() {
        countDownTimer.cancel()
        msStopped += Long.MAX_VALUE - msUntilFinished
    }

    override fun onReset() {
        countDownTimer.cancel()
        elapsedTime = initialElapsedDurationInMillis
        msStopped = 0
        timerTickStateFlow.value = "00:00"
    }

    override fun getElapsedDuration(): Long {
        return elapsedTime
    }
}
/* </CUSTOM_CALL_HEADER> */
