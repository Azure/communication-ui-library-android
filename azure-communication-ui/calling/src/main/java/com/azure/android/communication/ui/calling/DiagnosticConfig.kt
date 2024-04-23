// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.ui.calling.implementation.BuildConfig

internal class DiagnosticConfig {
    val tags: Array<String> by lazy { arrayOf(getApplicationId()) }

    private fun getApplicationId(): String {
        val callingCompositeVersionName = BuildConfig.UI_SDK_VERSION
        val baseTag = "ac"
        // Tag template is: acXYYY/<version>
        // Where:
        // - X describes a platform, [r: web, i: iOS, a: Android]
        // - YYY describes what's running on this platform (optional):
        //      Y[0] is high-level artifact,
        //          [0: undefined, 1: AzureCommunicationLibrary, 2: ACS SampleApp]
        //      Y[1] is specific implementation,
        //          [0: undefined, 1: Call Composite, 2: Chat Composite, 3: CallWithChatComposite, 4: UI Components]
        //      Y[2] is reserved for implementation details,
        //          [0: undefined]
        // - Version of this artifact
        return "${baseTag}a110/$callingCompositeVersionName"
    }
}
