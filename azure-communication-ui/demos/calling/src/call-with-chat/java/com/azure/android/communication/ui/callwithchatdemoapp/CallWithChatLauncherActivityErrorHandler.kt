// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp

import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeErrorEvent
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeEventHandler
import java.lang.ref.WeakReference

class CallWithChatLauncherActivityErrorHandler(alertHandler: AlertHandler) :
    CallWithChatCompositeEventHandler<CallWithChatCompositeErrorEvent> {

    private val activityWr: WeakReference<AlertHandler> = WeakReference(alertHandler)

    override fun handle(it: CallWithChatCompositeErrorEvent) {
        println("================= application is logging exception =================")
        println(it.cause)
        println(it.errorCode)
        activityWr.get()?.showAlert(it.errorCode.toString() + " " + it.cause?.message)
        println("====================================================================")
    }
}
