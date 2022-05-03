// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.calling.CallingEventHandler
import com.azure.android.communication.ui.calling.configuration.events.CommunicationUIErrorEvent
import java.lang.ref.WeakReference

// Handles forwarding of error messages to the CallLauncherActivity
//
// CallLauncherActivity is loosely coupled and will detach the weak reference after disposed.
class CallLauncherActivityErrorHandler(callLauncherActivity: CallLauncherActivity) :
    CallingEventHandler<CommunicationUIErrorEvent> {

    private val activityWr: WeakReference<CallLauncherActivity> =
        WeakReference(callLauncherActivity)

    override fun handle(it: CommunicationUIErrorEvent) {
        println("================= application is logging exception =================")
        println(it.cause)
        println(it.errorCode)
        activityWr.get()?.showAlert(it.errorCode.toString() + " " + it.cause?.message)
        println("====================================================================")
    }
}
