// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.diagnostics

internal class PerformanceDiagnostics {
<<<<<<< HEAD
    companion object {
        const val CALL_SCREEN_LOADING = "CALL_SCREEN_LOADING"

        fun startTrackingMetric(name: String) {
        }

        fun finishTrackingMetric(name: String) {
        }

        fun sendEvent(event: String) {
        }
=======
    fun trackMetric(name: String, value: Double) {
        // It is a stub. Do not add implementation here
>>>>>>> db9fd66 (Initial Performance Telemetry backbone)
    }
}
