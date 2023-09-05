package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat


class HandleNotification: BroadcastReceiver()
{
    private val TAG = "FirebaseTest "

    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancelAll()
        Log.i(TAG, "HandleNotification.onReceive()")

        if (intent.extras != null) {
            val action = intent.getStringExtra("action")
            Log.i(TAG, String.format("action:%s", action))
            assert(action != null)

            CallLauncherActivity.callLauncherActivity?.answerCall()

            /*
            val intent = Intent(context, CallLauncherActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("action", action)
            context.startActivity(intent)
             */


           // context.stopService(Intent(context, HandleNotification::class.java))
        }
    }

}