// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity
import com.azure.android.communication.ui.calling.presentation.PiPCallCompositeActivity
import java.lang.ref.WeakReference

internal class InCallService : Service() {
    private val IN_CALL_CHANNEL_ID = "com.azure.android.communication.ui.service.calling.in_call"
    private val binder = WeakReference(InCallServiceBinder(WeakReference(this)))

    override fun onBind(intent: Intent): IBinder? {
        println("InCallService onBind")
        val enableMultitasking = intent.getBooleanExtra("enableMultitasking", false)
        val enableSystemPiPWhenMultitasking = intent.getBooleanExtra("enableSystemPiPWhenMultitasking", false)
        val instanceId = intent.getIntExtra(CallCompositeActivity.KEY_INSTANCE_ID, 0)

        startInCallNotification(instanceId, enableMultitasking, enableSystemPiPWhenMultitasking)
        return binder.get()
    }

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int,
    ): Int {
        println("InCallService onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        println("InCallService onDestroy")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        println("InCallService onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onCreate() {
        println("InCallService onCreate")
        super.onCreate()
        createInCallNotificationChannel()
    }

    private fun startInCallNotification(
        instanceId: Int,
        enableMultitasking: Boolean,
        enableSystemPiPWhenMultitasking: Boolean,
    ) {
        var activityClass: Class<*> = CallCompositeActivity::class.java

        if (enableMultitasking) {
            activityClass = MultitaskingCallCompositeActivity::class.java
        }
        if (enableSystemPiPWhenMultitasking) {
            activityClass = PiPCallCompositeActivity::class.java
        }

        val intent =
            Intent(this, activityClass)
                .putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification: Notification =
            NotificationCompat.Builder(this, IN_CALL_CHANNEL_ID)
                .setContentTitle(this.getText(R.string.azure_communication_ui_calling_service_notification_title))
                .setContentText(this.getText(R.string.azure_communication_ui_calling_service_notification_message))
                .setSmallIcon(R.drawable.azure_communication_ui_calling_ic_fluent_call_16_filled)
                .setFullScreenIntent(pendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setOngoing(true)
                .setSound(null)
                .build()

        val notificationId = 1
        startForeground(notificationId, notification)
    }

    private fun createInCallNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "AzureCalling Call Status"
            val description = "Provides a notification for on-going calls"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(IN_CALL_CHANNEL_ID, name, importance)
            channel.description = description
            channel.setSound(null, null)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun destroyNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }
}

internal class InCallServiceBinder(val mInCallService: WeakReference<InCallService>) : Binder() {
    internal fun getService(): InCallService? {
        return mInCallService.get()
    }
}
