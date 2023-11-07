// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationEventType
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.callingcompositedemoapp.telecom.TelecomConnectionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DemoFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(CallLauncherActivity.TAG, token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val telecomConnectionManager = TelecomConnectionManager.getInstance(
            applicationContext,
            TelecomConnectionManager.PHONE_ACCOUNT_ID
        )
        Log.d(CallLauncherActivity.TAG, remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty()) {
            val pushNotificationInfo = CallCompositePushNotificationInfo(remoteMessage.data)
            if (pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_CALL ||
                pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_GROUP_CALL
            ) {
                Log.d(
                    CallLauncherActivity.TAG,
                    pushNotificationInfo.eventType.toString() + " handleIncomingCall"
                )
                CallLauncherActivity.callCompositeEvents?.handleIncomingCall(remoteMessage.data)

                telecomConnectionManager.startIncomingConnection(
                    applicationContext,
                    pushNotificationInfo,
                    false
                )
            } else if (pushNotificationInfo.eventType == CallCompositePushNotificationEventType.STOP_RINGING) {
                // Check if connection established else end connection
//                telecomConnectionManager.endConnection(
//                    applicationContext
//                )
            }
        }
    }
}
