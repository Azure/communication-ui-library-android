// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.configuration.events.ErrorEvent
import java.lang.ref.WeakReference

// Handles forwarding of error messages to the CallLauncherActivity
//
// CallLauncherActivity is loosely coupled and will detach the weak reference after disposed.
class CallLauncherActivityErrorHandler(callLauncherActivity: CallLauncherActivity) :
    CallingEventHandler<ErrorEvent<CallCompositeErrorCode>> {

    private val activityWr: WeakReference<CallLauncherActivity> =
        WeakReference(callLauncherActivity)

    override fun handle(it: ErrorEvent<CallCompositeErrorCode>) {
        println("================= application is logging exception =================")
        println(it.cause)
        println(it.errorCode)
        activityWr.get()?.showAlert(it.errorCode.toString() + " " + it.cause?.message)
        println("====================================================================")
    }
}
