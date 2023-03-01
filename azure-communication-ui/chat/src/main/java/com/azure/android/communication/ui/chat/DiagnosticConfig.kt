// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

internal class DiagnosticConfig {
    val tag: String by lazy { getApplicationId() }

    private fun getApplicationId(): String {
        val chatCompositeVersionName = "1.0.0-beta.2"
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
        return "${baseTag}a120/$chatCompositeVersionName"
    }
}
