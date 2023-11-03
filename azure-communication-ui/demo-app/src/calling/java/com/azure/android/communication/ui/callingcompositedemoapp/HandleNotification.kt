package com.azure.android.communication.ui.callingcompositedemoapp

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

            if (action == "answer") {
                val callComposite = CallCompositeProvider.getInstance().getCallComposite(context.applicationContext)
                callComposite.acceptIncomingCall(context.applicationContext)
            } else if (action == "decline") {
                val callComposite = CallCompositeProvider.getInstance().getCallComposite(context.applicationContext)
                callComposite.declineIncomingCall()
            }

            // context.stopService(Intent(context, HandleNotification::class.java))
        }
    }
}
