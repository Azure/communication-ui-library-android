// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Log
import com.azure.android.communication.ui.calling.diagnostics.PerformanceDiagnostics
import com.azure.android.communication.ui.calling.diagnostics.TrackMetricHandler
import com.microsoft.appcenter.analytics.Analytics


internal class PerformanceDiagnosticsClient {
    fun init() {
        PerformanceDiagnostics.callback = object : TrackMetricHandler {
            override fun trackMetric(name: String, value: Long) {
                Log.d("PerformanceDiagnostics", "$name: $value")
            }

            override fun trackEvent(event: String) {
                Analytics.trackEvent(event)
            }

        }
    }
}
