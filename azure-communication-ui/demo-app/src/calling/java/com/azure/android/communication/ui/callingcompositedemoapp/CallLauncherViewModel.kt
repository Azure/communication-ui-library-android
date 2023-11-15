// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeStartCallOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class CallLauncherViewModel : ViewModel() {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)
    var isExitRequested = false
    private val callStateEventHandler = CallStateEventHandler(callCompositeCallStateStateFlow)
    private var exitEventHandler: CallExitEventHandler? = null
    private var errorHandler: CallLauncherActivityErrorHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedHandler? = null
    private var exitedCompositeToAcceptCall: Boolean = false
    private var callCompositeManager = CallCompositeManager.getInstance()
    private var callComposite: CallComposite? = null

    fun destroy() {
        unsubscribe()
        callCompositeManager.destroy()
    }

    companion object {
        var callComposite: CallComposite? = null
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

        callComposite.addOnPictureInPictureChangedEventHandler {
            println("addOnMultitaskingStateChangedEventHandler it.isInPictureInPicture: " + it.isInPictureInPicture)
        }

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
            else null

        var skipSetup = SettingsFeatures.getSkipSetupScreenFeatureOption()
        val remoteOptions = if (locator == null && !participantMri.isNullOrEmpty()) {
            val participantMris = participantMri.split(",")
            val startCallOption = CallCompositeStartCallOptions(participantMris)
            CallCompositeRemoteOptions(startCallOption, communicationTokenCredential, displayName)
        } else {
            else if (roomId != null && roomRoleHint != null) CallCompositeRoomLocator(roomId)
            else throw IllegalArgumentException("Cannot launch call composite with provided arguments.")

        val remoteOptions =
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
            .setSkipSetupScreen(SettingsFeatures.getSkipSetupScreenFeatureOption())
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callCompositeExitSuccessStateFlow.value = false
        isExitRequested = false

        callComposite?.launch(context, remoteOptions, localOptions)
    }

    private fun subscribeToEvents(context: Context) {
        errorHandler = CallLauncherActivityErrorHandler(
            context,
            callComposite!!
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

    private fun createCallComposite(context: Context): CallComposite {
        if (callComposite != null) {
            return callComposite!!
        }
        SettingsFeatures.initialize(context.applicationContext)

        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }
        val selectedCallScreenOrientation = SettingsFeatures.callScreenOrientation()
        val callScreenOrientation = selectedCallScreenOrientation?.let { SettingsFeatures.orientation(it) }
        val selectedSetupScreenOrientation = SettingsFeatures.setupScreenOrientation()
        val setupScreenOrientation = selectedSetupScreenOrientation?.let { SettingsFeatures.orientation(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(CallCompositeLocalizationOptions(locale!!, SettingsFeatures.getLayoutDirection()))
            .setupScreenOrientation(setupScreenOrientation)
            .callScreenOrientation(callScreenOrientation)

        var callComposite = callCompositeManager.getCallComposite()
        if (callComposite == null) {
            callComposite = callCompositeManager.createCallComposite()
        }

        this.callComposite = callComposite

        subscribeToEvents(context)

        callCompositeBuilder.multitasking(CallCompositeMultitaskingOptions(true, true))

        val newCallComposite = callCompositeBuilder.build()

        callComposite = newCallComposite
        return newCallComposite
    }

    fun displayCallCompositeIfWasHidden(context: Context) {
        callComposite?.displayCallCompositeIfWasHidden(context)
    }

    fun acceptIncomingCall(applicationContext: Context) {
        // end existing call if any
        Log.d(CallLauncherActivity.TAG, "CallLauncherViewModel acceptIncomingCall")
        createCallComposite(applicationContext)

        if (callComposite?.callState != CallCompositeCallStateCode.NONE) {
            exitedCompositeToAcceptCall = true
            callComposite?.dismiss()
            return
        }

        exitedCompositeToAcceptCall = false

        val skipSetup = SettingsFeatures.getSkipSetupScreenFeatureOption()

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

    private fun unsubscribe() {
        callComposite?.let { composite ->
            composite.removeOnCallStateChangedEventHandler(callStateEventHandler)
            composite.removeOnErrorEventHandler(errorHandler)
            composite.removeOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
            composite.removeOnDismissedEventHandler(exitEventHandler)
        }
    }

    fun callHangup() {
        isExitRequested = true
        callComposite?.dismiss()
    }
}

class CallStateEventHandler(private val callCompositeCallStateStateFlow: MutableStateFlow<String>) : CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
    override fun handle(callStateEvent: CallCompositeCallStateChangedEvent) {
        callCompositeCallStateStateFlow.value = callStateEvent.code.toString()
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
    }
}
