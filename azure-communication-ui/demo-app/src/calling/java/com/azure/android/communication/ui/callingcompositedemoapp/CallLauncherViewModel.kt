// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeStartCallOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class CallLauncherViewModel : ViewModel(), OnErrorEventHandler {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeShowAlertStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)
    var isExitRequested = false
    val userReportedIssueEvent = MutableStateFlow<CallCompositeUserReportedIssueEvent?>(null)

    private val callStateEventHandler = CallStateEventHandler(callCompositeCallStateStateFlow)
    private var exitEventHandler: CallExitEventHandler? = null
    private var userReportedIssueEventHandler: OnUserReportedEventErrorHandler? = null
    private var errorHandler: CallLauncherActivityErrorHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedHandler? = null
    private var exitedCompositeToAcceptCall: Boolean = false
    private var callCompositeManager = CallCompositeManager.getInstance()
    private var callComposite: CallComposite? = null
    private var callCompositePictureInPictureChangedEvent: PiPListener? = null
    private var audioSelectionChangedEvent: AudioSelectionSelection? = null

    fun destroy() {
        unsubscribe()
        callCompositeManager.destroy()
        callComposite = null
    }

    fun onCompositeDismiss() {
        unsubscribe()
        callComposite = null
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
        createCallComposite(context)
        callCompositeShowAlertStateStateFlow.value = ""
        if (!SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(context).hide()
        } else {
            EndCompositeButtonView.get(context).show(this)
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

        var skipSetup = SettingsFeatures.getSkipSetupScreenFeatureValue()
        val remoteOptions = if (locator == null && !participantMri.isNullOrEmpty()) {
            val participantMris = participantMri.split(",")
            val startCallOption = CallCompositeStartCallOptions(participantMris)
            CallCompositeRemoteOptions(startCallOption, communicationTokenCredential, displayName)
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

    private fun subscribeToEvents(context: Context) {
        callCompositePictureInPictureChangedEvent = PiPListener()
        callComposite?.addOnPictureInPictureChangedEventHandler(
            callCompositePictureInPictureChangedEvent!!
        )

        errorHandler = CallLauncherActivityErrorHandler(
            this
        )
        callComposite?.addOnErrorEventHandler(errorHandler)

        remoteParticipantJoinedEvent = RemoteParticipantJoinedHandler(callComposite!!, context)
        callComposite?.addOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)

        exitEventHandler = CallExitEventHandler(
            callCompositeExitSuccessStateFlow,
            callCompositeCallStateStateFlow,
            this
        )

        callComposite?.addOnUserReportedEventHandler {
            userReportedIssueEvent.value = it
        }

        callComposite?.addOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite?.addOnDismissedEventHandler(exitEventHandler)

        audioSelectionChangedEvent = AudioSelectionSelection()
        callComposite?.addOnAudioSelectionChangedEventHandler(audioSelectionChangedEvent!!)
    }

    fun handleIncomingCall(
        applicationContext: Context
    ) {
        if (!SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(applicationContext).hide()
        } else {
            EndCompositeButtonView.get(applicationContext).show(this)
        }
        callCompositeExitSuccessStateFlow.value = false
        isExitRequested = false
    }

    fun close() {
        callComposite?.dismiss()
    }

    fun getCallHistory(context: Context): List<CallCompositeCallHistoryRecord> {
        return (
            callComposite
                ?: createCallComposite(context)
            ).getDebugInfo(context).callHistoryRecords
    }

    fun createCallComposite(context: Context): CallComposite {
        if (callComposite != null) {
            return callComposite!!
        }

        var callComposite = callCompositeManager.getCallComposite()
        if (callComposite == null) {
            callComposite = callCompositeManager.createCallComposite()
        }

        this.callComposite = callComposite

        subscribeToEvents(context)
        return callComposite
    }

    fun displayCallCompositeIfWasHidden(context: Context) {
        callComposite?.displayCallCompositeIfWasHidden(context)
    }

    fun acceptIncomingCall(applicationContext: Context) {
        // end existing call if any
        CallCompositeManager.getInstance().hideIncomingCallUI()
        Log.d(CallLauncherActivity.TAG, "CallLauncherViewModel acceptIncomingCall")
        createCallComposite(applicationContext)

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

    private fun unsubscribe() {
        callComposite?.let { composite ->
            composite.removeOnPictureInPictureChangedEventHandler(
                callCompositePictureInPictureChangedEvent
            )
            composite.removeOnUserReportedEventHandler(userReportedIssueEventHandler)
            composite.removeOnCallStateChangedEventHandler(callStateEventHandler)
            composite.removeOnErrorEventHandler(errorHandler)
            composite.removeOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
            composite.removeOnDismissedEventHandler(exitEventHandler)
            composite.removeOnAudioSelectionChangedEventHandler(audioSelectionChangedEvent)
        }
    }

    fun callHangup() {
        isExitRequested = true
        callComposite?.dismiss()
    }

    fun getLastCallId(context: Context): String {
        return callComposite?.getDebugInfo(context)?.callHistoryRecords?.lastOrNull()?.callIds?.lastOrNull()
            ?.toString() ?: ""
    }

    override fun showError(message: String) {
        callCompositeShowAlertStateStateFlow.value = message
    }
}

interface OnErrorEventHandler {
    fun showError(message: String)
}

class CallStateEventHandler(private val callCompositeCallStateStateFlow: MutableStateFlow<String>) :
    CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
    override fun handle(callStateEvent: CallCompositeCallStateChangedEvent) {
        callCompositeCallStateStateFlow.value = callStateEvent.code.toString()
        Log.d(
            CallLauncherActivity.TAG,
            "CallStateEventHandler handle demo app: ${callStateEvent.code} ${callStateEvent.callEndReasonCode} ${callStateEvent.callEndReasonSubCode}"
        )
    }
}

class UserReportedIssueHandler : CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    var lastEvent = MutableStateFlow<CallCompositeUserReportedIssueEvent?>(null)
    override fun handle(eventArgs: CallCompositeUserReportedIssueEvent?) {
        lastEvent.value = eventArgs
    }
}

class CallExitEventHandler(
    private val exitStateFlow: MutableStateFlow<Boolean>,
    private val callCompositeCallStateStateFlow: MutableStateFlow<String>,
    private val callLauncherViewModel: CallLauncherViewModel,
) : CallCompositeEventHandler<CallCompositeDismissedEvent> {
    override fun handle(event: CallCompositeDismissedEvent) {
        exitStateFlow.value = true && callLauncherViewModel.isExitRequested
        event.errorCode?.let {
            callCompositeCallStateStateFlow.value = it.toString()
        }
        CallCompositeManager.getInstance().onCompositeDismiss()
        callLauncherViewModel.onCompositeDismiss()
    }
}

class PiPListener : CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent> {
    override fun handle(event: CallCompositePictureInPictureChangedEvent) {
        println("addOnMultitaskingStateChangedEventHandler it.isInPictureInPicture: ")
    }
}

class AudioSelectionSelection : CallCompositeEventHandler<CallCompositeAudioSelectionChangedEvent> {
    override fun handle(event: CallCompositeAudioSelectionChangedEvent) {
        println("addOnAudioSelectionChangedEventHandler it: " + event.selectionType)
        CallCompositeManager.getInstance().onAudioSelectionChanged(event.selectionType)
    }
}

class OnUserReportedEventErrorHandler(val context: Context) :
    CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    override fun handle(event: CallCompositeUserReportedIssueEvent) {
        val message = """
        User reported issue:
        ${event.userMessage}
        ${event.logFiles.joinToString(", ") { it.name }}
        ${event.history.joinToString("\n") { it.toString() }}
        """.trimIndent()

        // Show Toast message
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "User Issue Channel"
            val channelDescription = "Notifications for user reported issues"
            val channelId = "CallLauncherActivity"
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                description = channelDescription
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create and show the notification
        val notification = NotificationCompat.Builder(context, "CallLauncherActivity")
            .setContentTitle("User reported issue")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notification)
    }
}
