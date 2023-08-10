// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.platform.app.InstrumentationRegistry

object NetworkUtils {
    private val TIMED_OUT_VALUE = 300L

    fun disableNetwork() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.run {
            executeShellCommand("svc wifi disable")
            executeShellCommand("svc data disable")
        }
    }

    fun enableNetworkThatWasDisabled(onNetworkAvailable: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().run {
            uiAutomation.run {
                executeShellCommand("svc wifi enable")
                executeShellCommand("svc data enable")
            }
            val cm = targetContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var timeOut = 0L

            timeout@ while (timeOut < TIMED_OUT_VALUE) {
                val activeNetwork = cm.activeNetwork
                val networkCap = cm.getNetworkCapabilities(activeNetwork)

                if (activeNetwork != null && networkCap != null &&
                    networkCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                ) {
                    onNetworkAvailable()
                    break@timeout
                }
                Thread.sleep(100)
                timeOut++
            }
        }
    }
}
