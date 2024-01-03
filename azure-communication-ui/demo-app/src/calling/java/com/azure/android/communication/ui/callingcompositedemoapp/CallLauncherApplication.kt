// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Application
import com.azure.android.communication.ui.callingcompositedemoapp.telecom.TelecomConnectionService

class CallLauncherApplication : Application() {
    var telecomConnectionServiceListener: TelecomConnectionService? = null
    var callCompositeManager: CallCompositeManager? = null
}
