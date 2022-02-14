package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import android.os.Build
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.diagnostics.FpsDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.diagnostics.MemoryViewer
import com.azure.android.communication.ui.utilities.FeatureFlagEntry
import com.azure.android.communication.ui.utilities.FeatureFlags

// / Extra rules for enabling the Diagnostics feature
fun conditionallyRegisterDiagnostics(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        context.resources.getBoolean(R.bool.diagnostics)
    ) {
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.diagnosticsFeature)
    }
}

// / Just a place to hold the Features outside global Namespace (Java Doesn't like)
class AdditionalFeatures private constructor() {
    companion object {
        val diagnosticsFeature = FeatureFlagEntry(
            labelId = R.string.diagnostics,
            start = {
                MemoryViewer.getMemoryViewer(it).show()
                FpsDiagnostics.getFpsDiagnostics(it).start()
            },
            end = {
                MemoryViewer.getMemoryViewer(it).hide()
                FpsDiagnostics.getFpsDiagnostics(it).stop()
            }
        )

        val secondaryThemeFeature = FeatureFlagEntry(
            // Will use default false here
            labelId = R.string.secondary_theme,
            start = {},
            end = {}
        )
    }
}
