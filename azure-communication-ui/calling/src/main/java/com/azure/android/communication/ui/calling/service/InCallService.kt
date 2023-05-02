// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity

internal class InCallService : Service() {

    private val IN_CALL_CHANNEL_ID = "com.azure.android.communication.ui.service.calling.in_call"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val instanceId = intent?.getIntExtra(CallCompositeActivity.KEY_INSTANCE_ID, -1) ?: -1
        startInCallNotification(instanceId)
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onCreate() {
        super.onCreate()
        createInCallNotificationChannel()
    }

    private fun startInCallNotification(instanceId: Int) {

        val notificationIntent = Intent(this, CallCompositeActivity::class.java).apply {
            putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId)
            putExtra("fromnotification", true)
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT )
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, IN_CALL_CHANNEL_ID)
            .setContentTitle(this.getText(R.string.azure_communication_ui_calling_service_notification_title))
            .setContentText(this.getText(R.string.azure_communication_ui_calling_service_notification_message))
            .setSmallIcon(R.drawable.azure_communication_ui_calling_ic_fluent_call_16_filled)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
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
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
