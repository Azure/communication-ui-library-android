// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomIntegration
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomOptions
import com.azure.android.communication.ui.callingcompositedemoapp.IncomingCallActivity.Companion.DISPLAY_NAME
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread

class CallCompositeManager : CallCompositeEvents {
    private var incomingCallEvent: IncomingCallEvent? = null
    private var incomingCallEndEvent: IncomingCallEndEvent? = null
    private var exitedCompositeToAcceptCall: Boolean = false
    private var callComposite: CallComposite? = null

    companion object {
        private var instance: CallCompositeManager? = null

        fun initialize() {
            if (instance == null) {
                instance = CallCompositeManager()
            }
        }

        fun getInstance(): CallCompositeManager {
            if (instance == null) {
                throw IllegalStateException("CallCompositeManager is not initialized")
            }
            return instance!!
        }
    }

    override fun getCallComposite(): CallComposite? {
        return callComposite
    }

    override fun showIncomingCallUI(incomingCallInfo: CallCompositeIncomingCallInfo, applicationContext: Context) {
        runOnUiThread {
            showNotificationForIncomingCall(incomingCallInfo, applicationContext)
        }
    }

    override fun hideIncomingCallUI(applicationContext: Context) {
        applicationContext.let { context ->
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1)
        }
    }

    override fun handleIncomingCall(
        data: Map<String, String>,
        acsToken: String,
        displayName: String,
        applicationContext: Context
    ) {
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val remoteOptions = CallCompositeRemoteOptions(
            CallCompositePushNotificationInfo(data),
            communicationTokenCredential,
            displayName
        )

        if (callComposite == null) {
            callComposite = createCallComposite(applicationContext)
        }

        callComposite?.handlePushNotification(
            applicationContext,
            remoteOptions
        )
    }

    override fun onCompositeDismiss(applicationContext: Context) {
        registerIncomingCallAndDispose(applicationContext)
    }

    override fun acceptIncomingCall(applicationContext: Context) {
        hideIncomingCallUI(applicationContext)
        if (callComposite?.callState != CallCompositeCallStateCode.NONE) {
            exitedCompositeToAcceptCall = true
            callComposite?.dismiss()
            return
        }

        exitedCompositeToAcceptCall = false
        createCallComposite(applicationContext)
        val skipSetup = SettingsFeatures.getSkipSetupScreenFeatureValue()
        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(skipSetup)
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callComposite?.acceptIncomingCall(applicationContext, localOptions)
    }

    fun declineIncomingCall(applicationContext: Context) {
        hideIncomingCallUI(applicationContext)
        callComposite?.declineIncomingCall()
    }

    fun destroy(applicationContext: Context) {
        if (exitedCompositeToAcceptCall) {
            acceptIncomingCall(applicationContext)
        } else {
            onActivityDestroy()
        }
    }

    fun onActivityDestroy() {
        unsubscribe()
        callComposite?.dispose()
        callComposite = null
        instance = null
    }

    fun createCallComposite(applicationContext: Context): CallComposite {
        if (callComposite != null) {
            return callComposite!!
        }

        SettingsFeatures.initialize(applicationContext)
        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }
        val selectedCallScreenOrientation = SettingsFeatures.callScreenOrientation()
        val callScreenOrientation =
            selectedCallScreenOrientation?.let { SettingsFeatures.orientation(it) }
        val selectedSetupScreenOrientation = SettingsFeatures.setupScreenOrientation()
        val setupScreenOrientation =
            selectedSetupScreenOrientation?.let { SettingsFeatures.orientation(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(CallCompositeLocalizationOptions(locale!!, SettingsFeatures.getLayoutDirection()))
            .setupScreenOrientation(setupScreenOrientation)
            .callScreenOrientation(callScreenOrientation)
            .multitasking(CallCompositeMultitaskingOptions(true, true))

        locale.let {
            callCompositeBuilder.localization(
                CallCompositeLocalizationOptions(
                    locale,
                    SettingsFeatures.getLayoutDirection()
                )
            )
        }

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        val telecomOptions =
            CallCompositeTelecomOptions(CallCompositeTelecomIntegration.APPLICATION_IMPLEMENTED_TELECOM_MANAGER)
        callCompositeBuilder.telecom(telecomOptions)

        callComposite = callCompositeBuilder.build()
        subscribeToEvents(applicationContext)
        return callComposite!!
    }

    private fun registerIncomingCallAndDispose(applicationContext: Context) {
        val acsToken =
            applicationContext.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
                .getString(CACHED_TOKEN, "")
        val userName =
            applicationContext.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
                .getString(CACHED_USER_NAME, "")
        registerFirebaseToken(acsToken!!, userName!!, true, applicationContext)
    }

    private fun showNotificationForIncomingCall(notification: CallCompositeIncomingCallInfo, applicationContext: Context) {
        Log.i(CallLauncherActivity.TAG, "Showing notification for incoming call")
        val resultIntent = Intent(applicationContext, CallLauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        resultIntent.action = "incoming_call"
        val stackBuilder = TaskStackBuilder.create(applicationContext)
        stackBuilder.addNextIntentWithParentStack(resultIntent)

        val resultPendingIntent =
            stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val answerCallIntent =
            Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        answerCallIntent.action = "answer"
        answerCallIntent.putExtra("action", "answer")
        stackBuilder.addNextIntent(answerCallIntent)
        val answerCallPendingIntent =
            stackBuilder.getPendingIntent(1200, PendingIntent.FLAG_IMMUTABLE)

        val declineCallIntent =
            Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        declineCallIntent.action = "decline"
        declineCallIntent.putExtra("action", "decline")
        stackBuilder.addNextIntent(declineCallIntent)
        val declineCallPendingIntent = stackBuilder.getPendingIntent(
            1201,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val content = java.lang.String.format(
            "%s",
            notification.callerDisplayName
        )

        val intent = Intent(applicationContext, IncomingCallActivity::class.java)
        intent.putExtra(DISPLAY_NAME, notification.callerDisplayName)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, "acs")
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(android.R.drawable.ic_menu_call)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Incoming Call")
                .setContentText(content)
                .addAction(
                    android.R.drawable.ic_menu_call,
                    applicationContext.getString(R.string.accept),
                    answerCallPendingIntent
                )
                .addAction(
                    android.R.drawable.ic_menu_call,
                    applicationContext.getString(R.string.decline),
                    declineCallPendingIntent
                )
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setPriority(NotificationManagerCompat.IMPORTANCE_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setOngoing(true)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(1, builder.build())
    }

    fun registerFirebaseToken(
        token: String,
        displayName: String,
        dispose: Boolean = false,
        applicationContext: Context
    ) {
        if (token.isEmpty()) {
            if (dispose) {
                destroy(applicationContext)
            }
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (dispose) {
                        destroy(applicationContext)
                    }
                    return@OnCompleteListener
                }
                val deviceRegistrationToken = task.result
                val callComposite = createCallComposite(applicationContext)
                callComposite.registerPushNotification(
                    applicationContext,
                    CallCompositePushNotificationOptions(
                        CommunicationTokenCredential(token),
                        deviceRegistrationToken,
                        displayName
                    )
                ) {
                    if (dispose) {
                        destroy(applicationContext)
                    }
                }
            }
        )
    }

    private fun subscribeToEvents(applicationContext: Context) {
        incomingCallEvent = IncomingCallEvent(applicationContext)
        callComposite?.addOnIncomingCallEventHandler(incomingCallEvent)

        incomingCallEndEvent = IncomingCallEndEvent(applicationContext)
        callComposite?.addOnIncomingCallEndEventHandler(incomingCallEndEvent)
    }

    private fun unsubscribe() {
        callComposite?.let { composite ->
            incomingCallEvent?.let {
                composite.removeOnIncomingCallEventHandler(incomingCallEvent)
            }
            incomingCallEndEvent?.let {
                composite.removeOnIncomingCallEndEventHandler(incomingCallEndEvent)
            }
        }
    }

    class IncomingCallEvent(private val applicationContext: Context) : CallCompositeEventHandler<CallCompositeIncomingCallEvent> {
        override fun handle(eventArgs: CallCompositeIncomingCallEvent) {
            Log.i(CallLauncherActivity.TAG, "Showing IncomingCallEvent")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                getInstance().showIncomingCallUI(eventArgs.incomingCallInfo, applicationContext)
            }
        }
    }

    class IncomingCallEndEvent(private val applicationContext: Context) : CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> {
        override fun handle(eventArgs: CallCompositeIncomingCallEndEvent?) {
            Log.i(CallLauncherActivity.TAG, "Dismissing IncomingCallEvent " + eventArgs?.code)
            getInstance().hideIncomingCallUI(applicationContext)
            getInstance().onCompositeDismiss(applicationContext)
        }
    }
}
