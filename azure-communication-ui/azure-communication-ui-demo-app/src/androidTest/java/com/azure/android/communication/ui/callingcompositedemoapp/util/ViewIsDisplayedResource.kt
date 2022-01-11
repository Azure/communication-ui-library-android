// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.os.SystemClock
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import junit.framework.AssertionFailedError

typealias ViewMatcherFunctionPtr = () -> ViewInteraction
class ViewIsDisplayedResource {
    private val DEFAULT_WAIT_TIME = 2000L
    private val TIMED_OUT_VALUE = 30000L

    @Throws(IllegalStateException::class)
    fun waitUntilViewIsDisplayed(idlingCheck: ViewMatcherFunctionPtr): ViewInteraction {
        var isReady = false
        var timeOut = 0L
        lateinit var viewInteraction: ViewInteraction

        while (!isReady && timeOut < TIMED_OUT_VALUE) {
            try {
                viewInteraction = idlingCheck()
                isReady = true
            } catch (ex: Throwable) {
                if (ex is AssertionFailedError || ex is NoMatchingViewException) {
                    SystemClock.sleep(DEFAULT_WAIT_TIME)
                    timeOut += DEFAULT_WAIT_TIME
                } else throw ex
            }
        }
        if (isReady) return viewInteraction
        throw IllegalStateException("Timed out waiting for view")
    }
}
