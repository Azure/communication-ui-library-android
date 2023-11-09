package com.azure.android.communication.ui.callingcompositedemoapp
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class HandleNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancelAll()
        Log.i(CallLauncherActivity.TAG, "HandleNotification.onReceive()")

        if (intent.extras != null) {
            val action = intent.getStringExtra("action")
            Log.i(CallLauncherActivity.TAG, String.format("action:%s", action))
            assert(action != null)
            context.stopService(Intent(context, HandleNotification::class.java))
            CallLauncherActivity.callCompositeEvents?.hideIncomingCallUI()

            if (action == "answer") {
                CallLauncherActivity.callCompositeEvents?.acceptIncomingCall()
            } else if (action == "decline") {
                CallLauncherActivity.callCompositeEvents?.getCallComposite()?.declineIncomingCall()
            }
        }
    }
}
