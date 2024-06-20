// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Application
import android.content.Context

class CallLauncherApplication : Application() {
    private var callCompositeManager: CallCompositeManager? = null

    fun getCallCompositeManager(context: Context): CallCompositeManager {
        if (callCompositeManager == null) {
            callCompositeManager = CallCompositeManager(context)
        }
        return callCompositeManager!!
    }
}
