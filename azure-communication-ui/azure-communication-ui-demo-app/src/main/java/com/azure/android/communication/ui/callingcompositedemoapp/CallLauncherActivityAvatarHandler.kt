// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.AvatarPersonaData
import com.azure.android.communication.ui.persona.CallingAvatarPersonaHandler
import java.lang.ref.WeakReference

class CallLauncherActivityAvatarHandler(callLauncherActivity: CallLauncherActivity) :
    CallingAvatarPersonaHandler {
    private val activityWr: WeakReference<CallLauncherActivity> = WeakReference(callLauncherActivity)

    override fun onFetchAvatarPersonaData(userIdentifier: String?): AvatarPersonaData {
        return AvatarPersonaData()
    }
}
