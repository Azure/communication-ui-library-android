// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeAvMode
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
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CallLauncherViewModel : ViewModel(), OnErrorEventHandler {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeShowAlertStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)
    val userReportedIssueEventHandler: UserReportedIssueHandler = UserReportedIssueHandler()

    var isExitRequested = false
    private val callStateEventHandler = CallStateEventHandler(callCompositeCallStateStateFlow)
    private var exitEventHandler: CallExitEventHandler? = null
    private var errorHandler: CallLauncherActivityErrorHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedHandler? = null
    private var exitedCompositeToAcceptCall: Boolean = false
    private var callCompositeManager = CallCompositeManager.getInstance()
    private var callComposite: CallComposite? = null
    private var callCompositePictureInPictureChangedEvent: PiPListener? = null
    private var audioSelectionChangedEvent: AudioSelectionSelection? = null

    fun destroy() {
        unsubscribeFromEvents()
        callCompositeManager.destroy()
        callComposite = null
    }

    fun onCompositeDismiss() {
        unsubscribeFromEvents()
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
            .setAvMode(CallCompositeAvMode.NORMAL)
            .setSkipSetupScreen(skipSetup)
            .setRoleHint(roomRoleHint)
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callCompositeExitSuccessStateFlow.value = false
        isExitRequested = false

        callComposite?.launch(context, remoteOptions, localOptions)

        // In 20 Seconds, we'll toast the DebugInfo to the screen
        displayDebugInfoIn20Seconds(context)
    }

    private fun displayDebugInfoIn20Seconds(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(20000)

            callComposite?.getDebugInfo(context)?.let {
                val result = """Calling UI Version: ${it.callingUIVersion}
                        Calling SDK Version: ${it.callingSDKVersion}                    
                        Call History (${it.callHistoryRecords.size}) 
                        Log Files (${it.logFiles.size})
                        ${it.takeScreenshot()?.name ?: "N/A"}"""
                result.split("\n").map { line -> line.trim() }.forEach {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    Log.i("ACSCallingUI", it)
                }
            }
        }
    }

    private fun subscribeToEvents(context: Context) {
        callCompositePictureInPictureChangedEvent = PiPListener()
        callComposite?.addOnPictureInPictureChangedEventHandler(callCompositePictureInPictureChangedEvent!!)

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
        callComposite?.addOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite?.addOnDismissedEventHandler(exitEventHandler)

        audioSelectionChangedEvent = AudioSelectionSelection()
        callComposite?.addOnAudioSelectionChangedEventHandler(audioSelectionChangedEvent!!)
        callComposite?.addOnUserReportedEventHandler(userReportedIssueEventHandler)
    }

    private fun unsubscribeFromEvents() {
        callComposite?.let { composite ->
            composite.removeOnPictureInPictureChangedEventHandler(callCompositePictureInPictureChangedEvent)
            composite.removeOnCallStateChangedEventHandler(callStateEventHandler)
            composite.removeOnErrorEventHandler(errorHandler)
            composite.removeOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
            composite.removeOnDismissedEventHandler(exitEventHandler)
            composite.removeOnAudioSelectionChangedEventHandler(audioSelectionChangedEvent)
            composite.removeOnUserReportedEventHandler(userReportedIssueEventHandler)
        }
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
        return (callComposite ?: createCallComposite(context)).getDebugInfo(context).callHistoryRecords
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

    fun callHangup() {
        isExitRequested = true
        callComposite?.dismiss()
    }

    fun getLastCallId(context: Context): String {
        return callComposite?.getDebugInfo(context)?.callHistoryRecords?.lastOrNull()?.callIds?.lastOrNull()?.toString() ?: ""
    }

    override fun showError(message: String) {
        callCompositeShowAlertStateStateFlow.value = message
    }
}

interface OnErrorEventHandler {
    fun showError(message: String)
}

class CallStateEventHandler(private val callCompositeCallStateStateFlow: MutableStateFlow<String>) : CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
    override fun handle(callStateEvent: CallCompositeCallStateChangedEvent) {
        callCompositeCallStateStateFlow.value = callStateEvent.code.toString()
        Log.d(CallLauncherActivity.TAG, "CallStateEventHandler handle demo app: ${callStateEvent.code} ${callStateEvent.callEndReasonCode} ${callStateEvent.callEndReasonSubCode}")
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
