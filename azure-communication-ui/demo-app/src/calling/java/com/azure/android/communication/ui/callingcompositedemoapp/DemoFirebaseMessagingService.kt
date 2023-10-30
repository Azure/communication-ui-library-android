package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Log
import com.azure.android.communication.calling.PushNotificationEventType
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
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
}