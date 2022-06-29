// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Log
import com.azure.android.communication.ui.calling.diagnostics.PerformanceDiagnostics
import com.azure.android.communication.ui.calling.diagnostics.TrackMetricHandler
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.analytics.EventProperties

internal class PerformanceDiagnosticsClient {
    fun init() {
        PerformanceDiagnostics.callback = object : TrackMetricHandler {
            override fun trackMetric(name: String, value: Long) {
                var properties: EventProperties = EventProperties().set(name, value)
                Analytics.trackEvent(name, properties)
            }
            lateinit var trace: Trace
            override fun startTrace(name: String) {
                trace = FirebasePerformance.startTrace(name)
            }

            override fun stopTrace() {
                trace.stop()
            }
        }
    }
}
