// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread

// Handles forwarding of error messages to the CallLauncherActivity
//
// CallLauncherActivity is loosely coupled and will detach the weak reference after disposed.
class CallLauncherActivityErrorHandler(
    private val context: Context,
    private val callComposite: CallComposite,
) :
    CallCompositeEventHandler<CallCompositeErrorEvent> {

    override fun handle(it: CallCompositeErrorEvent) {

        val lastCallId = callComposite.getDebugInfo(context).callHistoryRecords
            .lastOrNull()?.callIds?.lastOrNull()?.toString() ?: ""

        println("================= application is logging exception =================")
        println("call id: $lastCallId")
        println(it.cause)
        println(it.errorCode)

        runOnUiThread {
            val builder = AlertDialog.Builder(context).apply {
                setMessage("${it.errorCode} ${it.cause?.message}. Call id: $lastCallId")
                setTitle("Alert")
                setPositiveButton("OK") { _, _ ->
                }
            }
            builder.show()
        }
        println("====================================================================")
    }
}
