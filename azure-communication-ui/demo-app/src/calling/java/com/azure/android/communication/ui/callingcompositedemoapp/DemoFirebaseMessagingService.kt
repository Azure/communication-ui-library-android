// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.ActivityManager
import android.content.Context
import android.media.Ringtone
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationEventType
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DemoFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(CallLauncherActivity.TAG, token)
    }

    private var ringtone: Ringtone? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        CallCompositeManager.initialize(applicationContext)

        Log.d(CallLauncherActivity.TAG, "onMessageReceived")
        Log.d(CallLauncherActivity.TAG, remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty()) {
            val pushNotificationInfo = CallCompositePushNotificationInfo(remoteMessage.data)
            if (pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_CALL ||
                pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_GROUP_CALL
            ) {
                Log.d(CallLauncherActivity.TAG, "onMessageReceived - ${pushNotificationInfo.eventType}")
                Log.d(
                    CallLauncherActivity.TAG,
                    pushNotificationInfo.eventType.toString() + " handleIncomingCall"
                )

                if (!isForeground(applicationContext.packageName)) {
                    wakeApp()
                }
                CallCompositeManager.getInstance().handleIncomingCall(remoteMessage.data,
                    BuildConfig.ACS_TOKEN,
                    pushNotificationInfo.fromDisplayName)
            }
        }
    }

    private fun wakeApp() {
        val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenIsOn = pm.isInteractive // check if screen is on

        if (!screenIsOn) {
            val wakeLockTag: String = applicationContext.packageName + "WAKELOCK"
            val wakeLock = pm.newWakeLock(
                PowerManager.ON_AFTER_RELEASE, wakeLockTag
            )

            //acquire will turn on the display
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

            //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
            wakeLock.release()
        }
    }

    private fun isForeground(myPackage: String): Boolean {
        val manager = applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        val runningTaskInfo = manager?.getRunningTasks(1)
        return if (runningTaskInfo.isNullOrEmpty()) {
            Log.d(CallLauncherActivity.TAG, "isForeground: runningTaskInfo is null or empty")
            false
        } else {
            Log.d(CallLauncherActivity.TAG, "isForeground:" + runningTaskInfo[0].topActivity?.packageName)
            val componentInfo = runningTaskInfo[0].topActivity
            componentInfo!!.packageName == myPackage
        }
    }
}
