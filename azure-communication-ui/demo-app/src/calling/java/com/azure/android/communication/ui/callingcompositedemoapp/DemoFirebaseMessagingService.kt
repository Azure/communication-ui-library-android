package com.azure.android.communication.ui.callingcompositedemoapp

import android.R
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azure.android.communication.calling.PushNotificationEventType
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.configuration.events.CallCompositeIncomingCallListener
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DemoFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val pushNotificationData = PushNotificationInfo.fromMap(remoteMessage.data)
            if (pushNotificationData.eventType == PushNotificationEventType.INCOMING_CALL) {
                handleIncomingCall(remoteMessage.data)
            }
        }
    }

    private fun handleIncomingCall(data: MutableMap<String, String>) {
        val callComposite = CallCompositeProvider.getInstance().getCallComposite(applicationContext)

        callComposite.addIncomingCallListener(object : CallCompositeIncomingCallListener {
            override fun onIncomingCall(callId: String) {
                showNotificationForIncomingCall(PushNotificationInfo.fromMap(data))
            }
        })

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ BuildConfig.ACS_TOKEN }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions = CallCompositeRemoteOptions(
            CallCompositePushNotificationInfo.fromMap(data),
            communicationTokenCredential,
            BuildConfig.USER_NAME
        )

        callComposite.handlePushNotification(
            applicationContext,
            CallCompositePushNotificationInfo.fromMap(data),
            remoteOptions
        )
    }

    private fun showNotificationForIncomingCall(notification: PushNotificationInfo) {
        Log.i(TAG, "Showing notification for incoming call")
        val resultIntent = Intent(this, CallLauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)

        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)

        val answerCallIntent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        answerCallIntent.putExtra("action", "answer")
        val answerCallPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1200,
            answerCallIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val declineCallIntent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        declineCallIntent.putExtra("action", "decline")
        val declineCallPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1201,
            declineCallIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val content = java.lang.String.format(
            "%s: \n%s\n %s",
            last10(Utilities.toMRI(notification.from)),
            last10(notification.fromDisplayName),
            last10(Utilities.toMRI(notification.to))
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "acs")
            .setContentIntent(resultPendingIntent)
            .setSmallIcon(R.drawable.ic_menu_call)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentTitle("sss")
            .setContentText(content)
            .addAction(R.drawable.ic_menu_call, "Accept", answerCallPendingIntent)
            .addAction(R.drawable.ic_menu_call, "Decline", declineCallPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            .setOngoing(true)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build())
    }

    private fun last10(`in`: String?): String? {
        return if (`in` != null) {
            if (`in`.length >= 10) `in`.substring(`in`.length - 10) else `in`
        } else ""
    }
}