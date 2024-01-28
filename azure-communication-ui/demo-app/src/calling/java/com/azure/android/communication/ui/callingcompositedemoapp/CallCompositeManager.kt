// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomIntegration
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomOptions
import com.azure.android.communication.ui.callingcompositedemoapp.IncomingCallActivity.Companion.DISPLAY_NAME
import com.azure.android.communication.ui.callingcompositedemoapp.events.AudioSelectionEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.CallCompositeEventsHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.CallExitEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.CallStateEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.ErrorEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.IncomingCallEndEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.IncomingCallEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.PiPEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.events.RemoteParticipantJoinedEventHandler
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.telecom.TelecomConnectionManager
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class CallCompositeManager(private var applicationContext: Context?) : CallCompositeEventsHandler {
    private var telecomConnectionManager: TelecomConnectionManager? = null

    private var isExitRequested = false
    private val callStateEventHandler = CallStateEventHandler(this)
    private var exitEventHandler: CallExitEventHandler? = null
    private var errorHandler: ErrorEventHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedEventHandler? = null
    private var exitedCompositeToAcceptCall: Boolean = false
    private var callComposite: CallComposite? = null
    private var callCompositePictureInPictureChangedEvent: PiPEventHandler? = null
    private var audioSelectionChangedEvent: AudioSelectionEventHandler? = null
    private var incomingCallEventHandler: IncomingCallEventHandler? = null
    private var incomingCallEndEvent: IncomingCallEndEventHandler? = null

    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeShowAlertStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)

    fun telecomConnectionManager(telecomConnectionManager: TelecomConnectionManager) {
        this.telecomConnectionManager = telecomConnectionManager
    }

    fun isTelecomConnectionManagerInitialized(): Boolean {
        return telecomConnectionManager != null
    }

    fun launch(
        context: Context,
        acsToken: String,
        displayName: String,
        groupId: UUID?,
        roomId: String?,
        roomRoleHint: CallCompositeParticipantRole?,
        meetingLink: String?,
        participantMri: String?
    ) {
        createCallComposite()
        if (!SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(context).hide()
        } else {
            EndCompositeButtonView.get(context).show()
        }

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallCompositeJoinLocator? =
            if (groupId != null) CallCompositeGroupCallLocator(groupId)
            else if (meetingLink != null) CallCompositeTeamsMeetingLinkLocator(meetingLink)
            else if (roomId != null && roomRoleHint != null) CallCompositeRoomLocator(roomId)
            else null

        val skipSetup = SettingsFeatures.getSkipSetupScreenFeatureValue()
        val remoteOptions = if (locator == null && !participantMri.isNullOrEmpty()) {
            val participantMris = participantMri.split(",")
            CallCompositeRemoteOptions(participantMris.map { CommunicationIdentifier.fromRawId(it) }, communicationTokenCredential, displayName)
        } else {
            CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)
        }

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(skipSetup)
            .setRoleHint(roomRoleHint)
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callCompositeExitSuccessStateFlow.value = false
        isExitRequested = false

        callComposite?.launch(context, remoteOptions, localOptions)
    }

    fun close() {
        callComposite?.dismiss()
    }

    fun getCallHistory(context: Context): List<CallCompositeCallHistoryRecord> {
        return (callComposite ?: createCallComposite()).getDebugInfo(context).callHistoryRecords
    }

    fun displayCallCompositeIfWasHidden(context: Context) {
        callComposite?.displayCallCompositeIfWasHidden(context)
    }

    fun acceptIncomingCall(applicationContext: Context) {
        hideIncomingCallUI()
        createCallComposite()

        if (callComposite?.callState != CallCompositeCallStateCode.NONE) {
            exitedCompositeToAcceptCall = true
            callComposite?.dismiss()
            return
        }

        exitedCompositeToAcceptCall = false
        val skipSetup = SettingsFeatures.getSkipSetupScreenFeatureValue()
        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(skipSetup) // Always skip setup screen for incoming call
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callComposite?.acceptIncomingCall(applicationContext, localOptions)
    }

    fun callHangup() {
        isExitRequested = true
        callComposite?.dismiss()
    }

    fun getLastCallId(context: Context): String {
        return callComposite?.getDebugInfo(context)?.callHistoryRecords?.lastOrNull()?.callIds?.lastOrNull()?.toString() ?: ""
    }

    fun getCallComposite(): CallComposite? {
        return callComposite
    }

    fun hideIncomingCallUI() {
        applicationContext?.let { context ->
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1)
        }
    }

    fun declineIncomingCall() {
        hideIncomingCallUI()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telecomConnectionManager?.declineCall()
        }
        callComposite?.declineIncomingCall()
    }

    fun destroy() {
        unsubscribe()
        callComposite?.dispose()
        callComposite = null
    }

    fun handleIncomingCall(
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
            callComposite = createCallComposite()
        }

        callComposite?.handlePushNotification(
            applicationContext,
            remoteOptions
        )
    }

    fun createCallComposite(): CallComposite {
        callCompositeShowAlertStateStateFlow.value = ""

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

        if (SettingsFeatures.getTelecomManagerFeatureValue()) {
            val telecomOptions =
                CallCompositeTelecomOptions(CallCompositeTelecomIntegration.APPLICATION_IMPLEMENTED_TELECOM_MANAGER)
            callCompositeBuilder.telecom(telecomOptions)
        }

        callComposite = callCompositeBuilder.build()
        subscribeToEvents(applicationContext!!)
        return callComposite!!
    }

    fun registerFirebaseToken(token: String, displayName: String, dispose: Boolean = false) {
        if (token.isEmpty()) {
            if (dispose) {
                destroy()
            }
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (dispose) {
                        destroy()
                    }
                    return@OnCompleteListener
                }
                val deviceRegistrationToken = task.result
                val callComposite = createCallComposite()
                callComposite.registerPushNotification(
                    applicationContext!!,
                    CallCompositePushNotificationOptions(
                        CommunicationTokenCredential(token),
                        deviceRegistrationToken,
                        displayName
                    )
                ).whenComplete { _, throwable ->
                    if (dispose && throwable == null) {
                        destroy()
                    }
                }
            }
        )
    }

    override fun onAudioSelectionChanged(audioSelection: CallCompositeAudioSelectionChangedEvent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        telecomConnectionManager?.setAudioSelection(audioSelection)
    }

    override fun showError(message: String) {
        callCompositeShowAlertStateStateFlow.value = message
    }

    override fun onExit(event: CallCompositeDismissedEvent) {
        callCompositeExitSuccessStateFlow.value = true && isExitRequested
        event.errorCode?.let {
            callCompositeCallStateStateFlow.value = it.toString()
        }
        onCompositeDismiss()
    }

    override fun onCallStateChanged(callStateEvent: CallCompositeCallStateChangedEvent) {
        callCompositeCallStateStateFlow.value = callStateEvent.code.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            SettingsFeatures.getTelecomManagerFeatureValue()
        ) {
            if (callStateEvent.code == CallCompositeCallStateCode.CONNECTING) {
                telecomConnectionManager?.startOutgoingConnection(
                    "Outgoing call"
                )
            }

            if (callStateEvent.code == CallCompositeCallStateCode.CONNECTED) {
                telecomConnectionManager?.setConnectionActive()
            }

            if (callStateEvent.code == CallCompositeCallStateCode.DISCONNECTING ||
                callStateEvent.code == CallCompositeCallStateCode.DISCONNECTED
            ) {
                telecomConnectionManager?.endConnection()
            }
        }
    }

    override fun onIncomingCall(incomingCall: CallCompositeIncomingCallEvent) {
        Log.i(CallLauncherActivity.TAG, "Showing IncomingCallEvent")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telecomConnectionManager?.startIncomingConnection(
                incomingCall, false
            )
            showNotificationForIncomingCall(incomingCall)
        } else {
            showNotificationForIncomingCall(incomingCall)
        }
    }

    override fun onIncomingCallEnd(incomingCallEnd: CallCompositeIncomingCallEndedEvent) {
        Log.i(CallLauncherActivity.TAG, "Dismissing IncomingCallEvent " + incomingCallEnd.code)
        hideIncomingCallUI()
        onCompositeDismiss()
    }

    private fun subscribeToEvents(context: Context) {
        incomingCallEventHandler = IncomingCallEventHandler(this)
        incomingCallEndEvent = IncomingCallEndEventHandler(this)
        callCompositePictureInPictureChangedEvent = PiPEventHandler()
        remoteParticipantJoinedEvent = RemoteParticipantJoinedEventHandler(callComposite!!, context)
        exitEventHandler = CallExitEventHandler(this)
        audioSelectionChangedEvent = AudioSelectionEventHandler(this)
        errorHandler = ErrorEventHandler(this)

        callComposite?.addOnIncomingCallEventHandler(incomingCallEventHandler)
        callComposite?.addOnIncomingCallEndEventHandler(incomingCallEndEvent)
        callComposite?.addOnPictureInPictureChangedEventHandler(callCompositePictureInPictureChangedEvent!!)
        callComposite?.addOnErrorEventHandler(errorHandler)
        callComposite?.addOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
        callComposite?.addOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite?.addOnDismissedEventHandler(exitEventHandler)
        callComposite?.addOnAudioSelectionChangedEventHandler(audioSelectionChangedEvent!!)
    }

    private fun unsubscribe() {
        callComposite?.let { composite ->
            composite.removeOnPictureInPictureChangedEventHandler(callCompositePictureInPictureChangedEvent)
            composite.removeOnCallStateChangedEventHandler(callStateEventHandler)
            composite.removeOnErrorEventHandler(errorHandler)
            composite.removeOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
            composite.removeOnDismissedEventHandler(exitEventHandler)
            composite.removeOnAudioSelectionChangedEventHandler(audioSelectionChangedEvent)
            composite.removeOnIncomingCallEventHandler(incomingCallEventHandler)
            composite.removeOnIncomingCallEndEventHandler(incomingCallEndEvent)
        }
    }

    private fun onCompositeDismiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telecomConnectionManager?.endConnection()
        }
        registerIncomingCallAndDispose()
        unsubscribe()
    }

    private fun registerIncomingCallAndDispose() {
        if (SettingsFeatures.getRegisterPushOnExitFeatureValue()) {
            val acsToken =
                applicationContext!!.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
                    .getString(CACHED_TOKEN, "")
            val userName =
                applicationContext!!.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
                    .getString(CACHED_USER_NAME, "")
            registerFirebaseToken(acsToken!!, userName!!, true)
        }
    }

    private fun showNotificationForIncomingCall(notification: CallCompositeIncomingCallEvent) {
        applicationContext?.let { applicationContext ->
            Log.i(CallLauncherActivity.TAG, "Showing notification for incoming call")

            val resultIntent = Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = "incoming_call"
            }
            val resultPendingIntent = PendingIntent.getActivity(
                applicationContext, 0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val answerIntent = Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = "answer"
                putExtra("action", "answer")
            }
            val answerCallPendingIntent = PendingIntent.getActivity(
                applicationContext, 0,
                answerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val declineIntent = Intent(applicationContext, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = "decline"
                putExtra("action", "decline")
            }
            val declineCallPendingIntent = PendingIntent.getActivity(
                applicationContext, 0,
                declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val content = java.lang.String.format(
                "%s",
                notification.callerDisplayName
            )

            val intent = Intent(applicationContext, IncomingCallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
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
    }
}
