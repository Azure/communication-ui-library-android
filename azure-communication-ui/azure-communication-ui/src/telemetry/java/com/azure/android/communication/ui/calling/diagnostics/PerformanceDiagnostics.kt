// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.diagnostics

class PerformanceDiagnostics {

    companion object {
        const val CALL_SCREEN_LOADING = "CALL_SCREEN_LOADING"

        private val timingsStorage = mutableMapOf<String, Long>()

        var callback: TrackMetricHandler? = null

        fun startTrackingMetric(name: String) {
            val start = System.currentTimeMillis()
            timingsStorage[name] = start
        }

        fun finishTrackingMetric(name: String) {
            timingsStorage[name]?.let {
                callback?.trackMetric(name, System.currentTimeMillis() - it)
            }
        }

        fun sendEvent(event: String) {
            callback?.trackEvent(event)
        }
    }
}

interface TrackMetricHandler {
    fun trackMetric(name: String, value: Long)
    fun trackEvent(event: String)
}
