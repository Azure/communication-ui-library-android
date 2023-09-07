package com.azure.android.communication.ui.calling.service

import android.content.Context
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallNotificationOptions

internal class CallNotificationManager {
    fun registerIncomingCallPushNotification(context: Context, notificationOptions: CallCompositeIncomingCallNotificationOptions, callingService: CallingService?) {
        if (callingService != null) {
            callingService.registerIncomingCallPushNotification(notificationOptions)
        } else {
            val callClient = CallClient()
            val callAgent = callClient.createCallAgent(context, notificationOptions.credential).get()
            callAgent.registerPushNotification(notificationOptions.deviceToken)
            callAgent.dispose()
            callClient.dispose()
        }
    }
}
