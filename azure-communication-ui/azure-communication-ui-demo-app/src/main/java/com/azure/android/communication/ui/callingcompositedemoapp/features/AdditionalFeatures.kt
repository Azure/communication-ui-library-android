// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

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
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.diagnosticsFeature)
    }
}

class AdditionalFeatures private constructor() {
    companion object {
        val diagnosticsFeature = FeatureFlagEntry(
            defaultBooleanId = R.bool.azure_communication_ui_feature_flag_test_false,
            labelId = R.string.diagnostics,
            start = {
                MagnifierViewer.getMagnifierViewer(it).show()
            },
            end = {
                MagnifierViewer.getMagnifierViewer(it).hide()
            },
            fallbackBoolean = false,
            fallbackLabel = "FPS, Memory Diagnostics"

        )

        val secondaryThemeFeature = FeatureFlagEntry(
            defaultBooleanId = R.bool.feature_theme_selection,
            // Will use default false here
            labelId = R.string.secondary_theme,
            start = {},
            end = {},
            fallbackBoolean = false,
            fallbackLabel = "Secondary theme"
        )
    }
}
