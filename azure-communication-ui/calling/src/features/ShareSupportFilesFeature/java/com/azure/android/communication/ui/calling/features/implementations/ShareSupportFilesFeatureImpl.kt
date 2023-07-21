package com.azure.android.communication.ui.calling.features.implementations

import android.content.Context
import android.widget.Toast
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.features.stubs.ShareSupportFilesFeature

class ShareSupportFilesFeatureImpl : ShareSupportFilesFeature() {
    override val testValue get() = "Enabled"

    override fun toastValues(context : Context, callClient: CallClient) {
        // Stub Implementation
        /*
        Toast.makeText(
            context,
            callClient.getdebugInfo().supportFiles.joinToString("\n"),
            Toast.LENGTH_SHORT
        ).show()

         */
    }
}