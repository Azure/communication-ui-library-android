// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.features

import android.content.Context
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.features.interfaces.SupportFilesFeature
import java.io.File

// Stub version of SupportFilesFeature.kt
internal class SupportFilesFeatureImpl : SupportFilesFeature() {
    override val isAvailable = true

    override fun getSupportFiles(client: CallClient, context: Context): List<File> {
        // We find with context instead of callClient here
        return context.filesDir.listFiles()?.filter { it.isFile && it.name.endsWith(".blog") }
            ?: emptyList()
    }
}
