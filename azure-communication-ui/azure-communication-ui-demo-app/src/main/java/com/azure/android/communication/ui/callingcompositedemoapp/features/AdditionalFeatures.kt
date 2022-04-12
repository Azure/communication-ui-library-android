// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.app.Application
import android.content.Context
import android.os.Build
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.diagnostics.MagnifierViewer
import com.azure.android.communication.ui.utilities.implementation.FeatureFlagEntry
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags

fun conditionallyRegisterDiagnostics(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        context.resources.getBoolean(R.bool.diagnostics)
    ) {
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.getDiagnosticFeature(context.applicationContext as Application))
    }
}

class AdditionalFeatures private constructor() {
    companion object {
        private lateinit var diagnosticsFeature : FeatureFlagEntry

        fun getDiagnosticFeature(application: Application) : FeatureFlagEntry {
            if (!this::diagnosticsFeature.isInitialized) {
                diagnosticsFeature = FeatureFlagEntry(
                    labelId = R.string.diagnostics,
                    start = {
                        MagnifierViewer.getMagnifierViewer(application).show()
                    },
                    end = {
                        MagnifierViewer.getMagnifierViewer(application).hide()
                    },
                    fallbackBoolean = false,
                    fallbackLabel = "FPS, Memory Diagnostics"

                )
            }
            return diagnosticsFeature
        }


        val secondaryThemeFeature = FeatureFlagEntry(
            // Will use default false here
            labelId = R.string.secondary_theme,
            start = {},
            end = {},
            fallbackBoolean = false,
            fallbackLabel = "Secondary theme"
        )
    }
}
