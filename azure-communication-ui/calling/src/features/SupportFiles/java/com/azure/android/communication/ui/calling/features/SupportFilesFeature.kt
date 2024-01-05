// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.features

import android.content.Context
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.features.interfaces.ISupportFilesFeature
import java.io.File
import java.util.Collections

// Implementation version of SupportFilesFeature
internal class SupportFilesFeature : ISupportFilesFeature() {
    override fun getSupportFiles(client: CallClient, context: Context): List<File> {
        return client.debugInfo.supportFiles ?: Collections.emptyList()
    }

    override fun IsAvailable(): Boolean {
        return true
    }
}