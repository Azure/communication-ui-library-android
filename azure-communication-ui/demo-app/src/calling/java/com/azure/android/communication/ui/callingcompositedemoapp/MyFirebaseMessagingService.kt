package com.azure.android.communication.ui.callingcompositedemoapp

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.azure.android.communication.calling.PushNotificationEventType
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.ui.callingcompositedemoapp.telecom_utils.CallHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@RequiresApi(Build.VERSION_CODES.M)
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseTest "
    }

    private var handler: CallHandler? = null

    private fun getCallHandler(): CallHandler {
        if(handler == null) {
            handler = CallHandler(applicationContext)
        }
        return handler!!
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageData: Map<String, String> = remoteMessage.data
        Log.d(TAG, "Incoming push notification$messageData")

        if (messageData.isNotEmpty()) {
            try {
                val notification = PushNotificationInfo.fromMap(messageData)
                CallLauncherViewModel.notificationData = notification
                if (notification.eventType == PushNotificationEventType.INCOMING_CALL) {
                    startIncomingCall(notification)
                }
                if (notification.eventType == PushNotificationEventType.STOP_RINGING) {
                    stopRingingCall()
                }
            } catch (_: Exception) {

            }
        }
        super.onMessageReceived(remoteMessage)
    }

    private fun startIncomingCall(notification: PushNotificationInfo) {
        try {
            getCallHandler().startIncomingCall(notification)
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Unable to receive call due to " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun stopRingingCall() {
        try {
            getCallHandler().endOngoingCall()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Unable to end call due to " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}