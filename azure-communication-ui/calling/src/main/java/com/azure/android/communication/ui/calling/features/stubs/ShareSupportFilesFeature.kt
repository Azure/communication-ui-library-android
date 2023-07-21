package com.azure.android.communication.ui.calling.features.stubs

import android.content.Context
import android.widget.Toast
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.features.AcsFeature

open class ShareSupportFilesFeature : AcsFeature {
    open val testValue get() = "Disabled"
    open fun toastValues(context : Context, callClient: CallClient) {
        // Stub Implementation
//        Toast.makeText(context.applicationContext, "Not enabled", Toast.LENGTH_SHORT).show()
    }
}