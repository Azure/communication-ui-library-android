// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.diagnostics

internal class PerformanceDiagnostics {

    companion object {
        const val CALL_SCREEN_LOADING = "CALL_SCREEN_LOADING"

        fun startTrackingMetric(name: String) {
        }

        fun finishTrackingMetric(name: String) {
        }

        fun sendEvent(event: String) {
        }
    }
}

interface TrackMetricHandler {
    fun trackMetric(name: String, value: Long)
    fun trackEvent(event: String)
}
