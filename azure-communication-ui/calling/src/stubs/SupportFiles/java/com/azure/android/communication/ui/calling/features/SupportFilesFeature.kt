package com.azure.android.communication.ui.calling.features

import android.content.Context
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.features.interfaces.ISupportFilesFeature
import java.io.File

// Stub version of SupportFilesFeature.kt
class SupportFilesFeature : ISupportFilesFeature() {
    override fun IsAvailable() = true

    override fun getSupportFiles(client: CallClient, context: Context): List<File> {
        // We find with context instead of callClient here
        return context.filesDir.listFiles()?.filter { it.isFile && it.name.endsWith(".blog") }
            ?: emptyList()
    }
}