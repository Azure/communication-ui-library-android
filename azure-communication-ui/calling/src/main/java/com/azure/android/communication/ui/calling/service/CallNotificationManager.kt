package com.azure.android.communication.ui.calling.service

import android.content.Context
import android.util.Log
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallNotificationOptions

internal class CallNotificationManager {
    fun registerIncomingCallPushNotification(context: Context, notificationOptions: CallCompositeIncomingCallNotificationOptions, callingService: CallingService?) {
        if (callingService != null) {
            callingService.registerIncomingCallPushNotification(notificationOptions)
        } else {
            val callClient = CallClient()
            val callAgent = callClient.createCallAgent(context, notificationOptions.credential).get()
            callAgent.registerPushNotification(notificationOptions.deviceToken)?.whenComplete { t, u ->
                if (u!= null) {
                    Log.d("CallingSDKWrapper", "registerIncomingCallPushNotification: fail")
                } else {
                    Log.d("CallingSDKWrapper", "registerIncomingCallPushNotification: success")
                }
            }
            callAgent.dispose()
            callClient.dispose()
        }
    }
}
