// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.ui.BuildConfig

internal class DiagnosticConfig {
    val tags: Array<String> by lazy { arrayOf(getApplicationId()) }

    private fun getApplicationId(): String {
        val callingCompositeVersionName = BuildConfig.UI_LIBRARY_VERSION_NAME
        return "aca110/$callingCompositeVersionName"
    }
}
