package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.MainActivity
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.configuration.events.ErrorEvent
import java.lang.ref.WeakReference

/// We take the Activity as a Param
class MainActivityErrorHandler(activity: MainActivity) : CallingEventHandler<ErrorEvent<CallCompositeErrorCode>> {

    /// But store in WeakReference to avoid leaks
    private val activityWr : WeakReference<MainActivity> = WeakReference(activity)

    override fun handle(it: ErrorEvent<CallCompositeErrorCode>) {
        println("================= application is logging exception =================")
        println(it.cause)
        println(it.errorCode)
        if (it.cause != null) {
            activityWr.get()?.showAlert(it.errorCode.toString() + " " + it.cause?.message)
        } else {
            activityWr.get()?.showAlert(it.errorCode.toString() + " " + it.cause?.message)
        }
        println("====================================================================")
    }
}