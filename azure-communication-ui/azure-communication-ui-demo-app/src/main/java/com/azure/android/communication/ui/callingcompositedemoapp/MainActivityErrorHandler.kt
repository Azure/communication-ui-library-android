package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.configuration.events.ErrorEvent
import java.lang.ref.WeakReference

// / Handles forwarding of error messages to the MainActivity
// / Stores MainActivity in a WeakReference in case it gets disposed
class MainActivityErrorHandler(activity: MainActivity) : CallingEventHandler<ErrorEvent<CallCompositeErrorCode>> {

    // / But store in WeakReference to avoid leaks
    private val activityWr: WeakReference<MainActivity> = WeakReference(activity)

    override fun handle(it: ErrorEvent<CallCompositeErrorCode>) {
        println("================= application is logging exception =================")
        println(it.cause)
        println(it.errorCode)
        activityWr.get()?.showAlert(it.errorCode.toString() + " " + it.cause?.message)
        println("====================================================================")
    }
}
