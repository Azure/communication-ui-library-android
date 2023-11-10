package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azure.android.communication.common.CommunicationIdentifier
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
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomIntegration
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomOptions
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.telecom.TelecomConnectionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread

class CallCompositeManager(private var applicationContext: Context?) : CallCompositeEvents {
    @RequiresApi(Build.VERSION_CODES.O)
    private val telecomConnectionManager: TelecomConnectionManager =
        TelecomConnectionManager.getInstance(
            applicationContext!!,
            TelecomConnectionManager.PHONE_ACCOUNT_ID
        )
    private var incomingCallEvent: IncomingCallEvent? = null
    private var incomingCallEndEvent: IncomingCallEndEvent? = null
    val mapOfDisplayNames = mutableMapOf<String, String>()

    companion object {
        private var instance: CallCompositeManager? = null
        private var callComposite: CallComposite? = null

        fun initialize(applicationContext: Context) {
            if (instance == null) {
                instance = CallCompositeManager(applicationContext)
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

    override fun showIncomingCallUI(incomingCallInfo: CallCompositeIncomingCallInfo) {
        runOnUiThread {
            showNotificationForIncomingCall(incomingCallInfo)
        }
    }

    override fun hideIncomingCallUI() {
        applicationContext?.let { context ->
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleIncomingCall(
        data: Map<String, String>,
        acsToken: String,
        displayName: String
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

        if(callComposite == null) {
            callComposite = createCallComposite()
        }

        Log.d(CallLauncherActivity.TAG, "handleIncomingCall$callComposite")
        callComposite?.handlePushNotification(
            applicationContext!!,
            remoteOptions
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCompositeDismiss() {
        instance?.telecomConnectionManager?.endConnection(applicationContext!!)
        registerFirebaseToken()
        destroy()
    }

    override fun onRemoteParticipantJoined(rawId: String) {
        mapOfDisplayNames[rawId]?.let { data ->
            getCallComposite()?.let {
                it.setRemoteParticipantViewData(
                    CommunicationIdentifier.fromRawId(rawId),
                    CallCompositeParticipantViewData()
                        .setDisplayName(data)
                )
            }
        }
    }

    override fun acceptIncomingCall() {
        if (applicationContext == null) {
            return
        }
        hideIncomingCallUI()
        if (callComposite?.callState != CallCompositeCallStateCode.NONE) {
            callComposite?.dismiss()
            return
        }

        createCallComposite()
        val skipSetup = SettingsFeatures.getSkipSetupScreenFeatureOption()

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(applicationContext!!))
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun declineIncomingCall() {
        hideIncomingCallUI()
        telecomConnectionManager.declineCall(applicationContext!!)
        callComposite?.declineIncomingCall()
        destroy()
    }

    fun destroy() {
        unsubscribe()
        callComposite?.dispose()
        callComposite = null
    }

    fun createCallComposite(): CallComposite {
        if (callComposite != null) {
            return callComposite!!
        }

        SettingsFeatures.initialize(applicationContext!!)
        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }
        val selectedCallScreenOrientation = SettingsFeatures.callScreenOrientation()
        val callScreenOrientation =
            selectedCallScreenOrientation?.let { SettingsFeatures.orientation(it) }
        val selectedSetupScreenOrientation = SettingsFeatures.setupScreenOrientation()
        val setupScreenOrientation =
            selectedSetupScreenOrientation?.let { SettingsFeatures.orientation(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .setupScreenOrientation(setupScreenOrientation)
            .callScreenOrientation(callScreenOrientation)

        locale?.let {
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
        subscribeToIncomingCallEvents(BuildConfig.USER_NAME)
        return callComposite!!
    }

    private fun showNotificationForIncomingCall(notification: CallCompositeIncomingCallInfo) {
        applicationContext?.let { applicationContext ->
            Log.i(CallLauncherActivity.TAG, "Showing notification for incoming call")
            val resultIntent = Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
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
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(applicationContext, "acs")
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(android.R.drawable.ic_menu_call)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Incoming Call")
                    .setContentText(content)
                    .addAction(
                        android.R.drawable.ic_menu_call,
                        "Accept",
                        answerCallPendingIntent
                    )
                    .addAction(
                        android.R.drawable.ic_menu_call,
                        "Decline",
                        declineCallPendingIntent
                    )
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setPriority(NotificationManagerCompat.IMPORTANCE_MAX)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                    .setOngoing(true)
                    .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(1, builder.build())
        }
    }

    fun registerFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(
                        applicationContext!!,
                        "Fetching FCM registration token failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnCompleteListener
                }

                val token = task.result
                val callComposite = createCallComposite()
                callComposite.registerPushNotification(
                    applicationContext!!,
                    CallCompositePushNotificationOptions(
                        CommunicationTokenCredential(BuildConfig.ACS_TOKEN),
                        token,
                        BuildConfig.USER_NAME
                    )
                )
            }
        )
    }

    private fun subscribeToIncomingCallEvents(displayName: String) {
        callComposite?.addOnCallStateChangedEventHandler {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (it.code == CallCompositeCallStateCode.CONNECTING) {
                    telecomConnectionManager.startOutgoingConnection(
                        applicationContext!!,
                        displayName,
                        false
                    )
                }

                if (it.code == CallCompositeCallStateCode.CONNECTED) {
                    this.telecomConnectionManager.setConnectionActive()
                }

                if (it.code == CallCompositeCallStateCode.DISCONNECTING
                    || it.code == CallCompositeCallStateCode.DISCONNECTED
                ) {
                    this.telecomConnectionManager.endConnection(applicationContext!!)
                }
            }
        }

        incomingCallEvent = IncomingCallEvent()
        callComposite?.addOnIncomingCallEventHandler(incomingCallEvent)

        incomingCallEndEvent = IncomingCallEndEvent()
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

    class IncomingCallEvent : CallCompositeEventHandler<CallCompositeIncomingCallEvent> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handle(eventArgs: CallCompositeIncomingCallEvent) {
            Log.i(CallLauncherActivity.TAG, "Showing IncomingCallEvent")
            getInstance().telecomConnectionManager.startIncomingConnection(
                instance?.applicationContext!!,
                eventArgs.incomingCallInfo, false
            )
        }
    }

    class IncomingCallEndEvent : CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handle(eventArgs: CallCompositeIncomingCallEndEvent?) {
            Log.i(CallLauncherActivity.TAG, "Dismissing IncomingCallEvent " + eventArgs?.code)
            getInstance().hideIncomingCallUI()
            getInstance().onCompositeDismiss()
        }
    }
}