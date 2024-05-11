// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.azure.android.communication.ui.calling.models.CallCompositePushNotification
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationEventType
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    private val sharedPreference by lazy {
        getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(CallLauncherActivity.TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(CallLauncherActivity.TAG, "onMessageReceived firebase push notification " + remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty()) {
            val pushNotificationInfo = CallCompositePushNotification(remoteMessage.data)
            Log.d(CallLauncherActivity.TAG, "onMessageReceived - ${pushNotificationInfo.eventType}")
            if (pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_CALL ||
                pushNotificationInfo.eventType == CallCompositePushNotificationEventType.INCOMING_GROUP_CALL
            ) {
                wakeAppIfScreenOff()
                sendIntent(IntentHelper.HANDLE_INCOMING_CALL_PUSH, remoteMessage)
            } else {
                sendIntent(IntentHelper.CLEAR_PUSH_NOTIFICATION, remoteMessage)
            }
        }
    }

    private fun sendIntent(tag: String, remoteMessage: RemoteMessage?) {
        if (CallLauncherActivity.isActivityRunning) {
            val intent = Intent(CallLauncherActivity.CALL_LAUNCHER_BROADCAST_ACTION)
            intent.putExtra("tag", tag)
            remoteMessage?.let {
                intent.putExtra("data", mapToString(it.data))
            }
            sendBroadcast(intent)
        } else {
            remoteMessage?.let {
                val acsIdentityToken = sharedPreference.getString(CACHED_TOKEN, "")
                val displayName = sharedPreference.getString(CACHED_USER_NAME, "")
                val application = application as CallLauncherApplication
                if (application.callCompositeManager == null) {
                    application.callCompositeManager = CallCompositeManager(applicationContext)
                }
                SettingsFeatures.initialize(applicationContext)
                application.callCompositeManager?.handleIncomingCall(
                    it.data,
                    acsIdentityToken!!,
                    displayName!!,
                    applicationContext
                )
            }
        }
    }

    private fun mapToString(map: Map<String, String>): String {
        return try {
            val objectMapper: ObjectMapper = jacksonObjectMapper()
            objectMapper.writeValueAsString(map)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            ""
        }
    }

    private fun wakeAppIfScreenOff() {
        val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenIsOn = pm.isInteractive // check if screen is on

        if (!screenIsOn) {
            val wakeLockTag: String = applicationContext.packageName + "WAKELOCK"
            val wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
                wakeLockTag
            )

            // acquire will turn on the display
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)

            wakeLock.release()
        }
    }
}
