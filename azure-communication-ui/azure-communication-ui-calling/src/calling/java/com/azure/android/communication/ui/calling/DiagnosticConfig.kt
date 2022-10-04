// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

internal class DiagnosticConfig {
    val tags: Array<String> by lazy { arrayOf(getApplicationId()) }

    private fun getApplicationId(): String {
        val callingCompositeVersionName = "1.0.0-beta.1-spike"
        return "aca110/$callingCompositeVersionName"
    }
}
