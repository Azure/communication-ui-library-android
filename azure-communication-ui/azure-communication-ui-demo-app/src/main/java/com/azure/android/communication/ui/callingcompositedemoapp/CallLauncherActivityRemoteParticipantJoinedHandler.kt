// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.events.RemoteParticipantJoinedEvent
import java.lang.ref.WeakReference

class CallLauncherActivityRemoteParticipantJoinedHandler(
    callLauncherActivity: CallLauncherActivity,
    callComposite: CallComposite,
) :
    CallingEventHandler<RemoteParticipantJoinedEvent> {

    private val activityWr: WeakReference<CallLauncherActivity> =
        WeakReference(callLauncherActivity)

    override fun handle(it: RemoteParticipantJoinedEvent) {
        println("================= application is logging exception =================")
        activityWr.get()?.showAlert("$it joined")
        println("====================================================================")
    }
}
