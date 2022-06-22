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
            callback?.startTrace(name)
        }

        fun finishTrackingMetric(name: String) {
            timingsStorage[name]?.let {
                callback?.trackMetric(name, System.currentTimeMillis() - it)
            }
            callback?.stopTrace()
        }
    }
}

interface TrackMetricHandler {
    fun trackMetric(name: String, value: Long)
    fun startTrace(name: String)
    fun stopTrace()
}
