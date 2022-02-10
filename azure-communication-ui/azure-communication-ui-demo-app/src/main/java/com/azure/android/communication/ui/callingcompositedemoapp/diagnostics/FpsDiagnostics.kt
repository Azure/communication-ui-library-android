// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.diagnostics

import android.app.Application
import com.microsoft.office.outlook.magnifierlib.Magnifier
import com.microsoft.office.outlook.magnifierlib.frame.FPSMonitorConfig

class FpsDiagnostics private constructor(
    private val context: Application,
) {

    companion object {
        private var fpsDiagnostics: FpsDiagnostics? = null
        fun getFpsDiagnostics(context: Application): FpsDiagnostics {
            if (fpsDiagnostics == null) {
                fpsDiagnostics = FpsDiagnostics(context)
            }
            return fpsDiagnostics!!
        }
    }

    fun start() {
        Magnifier.startMonitorFPS(
            FPSMonitorConfig.Builder(context)
                .lowPercentage(40 / 60f) // show red tips, (2.0f / 3.0f) by default
                .mediumPercentage(50 / 60f) // show yellow tips, (5.0f / 6.0f) by default
                .refreshRate(60f) // defaultDisplay.refreshRate by default
                .build()
        )
    }

    fun stop() {
        Magnifier.stopMonitorFPS()
    }
}
