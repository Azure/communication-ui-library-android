// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import java.lang.ref.WeakReference

// Handles forwarding of error messages to the CallLauncherActivity
//
// CallLauncherActivity is loosely coupled and will detach the weak reference after disposed.
class CallLauncherActivityErrorHandler(
    private val callComposite: CallComposite,
    callLauncherActivity: CallLauncherActivity
) :
    CallCompositeEventHandler<CallCompositeErrorEvent> {

    private val activityWr: WeakReference<CallLauncherActivity> =
        WeakReference(callLauncherActivity)

    override fun handle(it: CallCompositeErrorEvent) {
        println("================= application is logging exception =================")
        println("call id: " + (callComposite.diagnostics.lastKnownCallId ?: ""))
        println(it.cause)
        println(it.errorCode)
        activityWr.get()?.showAlert("${it.errorCode} ${it.cause?.message}. Call id: ${callComposite.diagnostics.lastKnownCallId ?: ""}")
        println("====================================================================")
    }
}
