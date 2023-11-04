// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Log
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationEventType
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DemoFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(CallLauncherActivity.TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(CallLauncherActivity.TAG, remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty()) {
            val pushNotificationInfo = CallCompositePushNotificationInfo(remoteMessage.data)
            if (pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_CALL) {
                Log.d(CallLauncherActivity.TAG, pushNotificationInfo.eventType.toString() + " handleIncomingCall")
                CallLauncherActivity.callCompositeEvents?.handleIncomingCall(remoteMessage.data)
            }
        }
    }
}
