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
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeStartCallOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class CallLauncherViewModel : ViewModel() {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)
    var isExitRequested = false
    private val callStateEventHandler = CallStateEventHandler(callCompositeCallStateStateFlow)
    private var exitEventHandler: CallExitEventHandler? = null
    private var incomingCallEvent: IncomingCallEvent? = null
    private var incomingCallEndEvent: IncomingCallEndEvent? = null
    private var errorHandler: CallLauncherActivityErrorHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedHandler? = null
    private var callComposite: CallComposite? = null
    private var exitedCompositeToAcceptCall: Boolean = false

    fun exitedCompositeToAcceptIncomingCall(): Boolean {
        return exitedCompositeToAcceptCall
    }

    fun destroy() {
        unsubscribe()
        callComposite?.dispose()
        callComposite = null
    }

    fun launch(
        context: Context,
        acsToken: String,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        participantMri: String?
    ) {
        createCallComposite(context)

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

        incomingCallEvent = IncomingCallEvent()
        callComposite?.addOnIncomingCallEventHandler(incomingCallEvent)

        incomingCallEndEvent = IncomingCallEndEvent()
        callComposite?.addOnIncomingCallEndEventHandler(incomingCallEndEvent)
    }

    fun handleIncomingCall(
        data: MutableMap<String, String>,
        applicationContext: Context,
        acsToken: String,
        displayName: String,
    ) {
        createCallComposite(applicationContext)

        if (!SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(applicationContext).hide()
        } else {
            EndCompositeButtonView.get(applicationContext).show(this)
        }

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        callCompositeExitSuccessStateFlow.value = false
        isExitRequested = false

        val remoteOptions = CallCompositeRemoteOptions(
            CallCompositePushNotificationInfo(data),
            communicationTokenCredential,
            displayName
        )
        callComposite?.handlePushNotification(
            applicationContext,
            remoteOptions
        )
    }

    fun acceptIncomingCall(applicationContext: Context) {

        if (callComposite?.callState != CallCompositeCallStateCode.NONE) {
            exitedCompositeToAcceptCall = true
            callComposite?.dismiss()
            return
        }

        exitedCompositeToAcceptCall = false

        // end existing call if any

        createCallComposite(applicationContext)
        var skipSetup = SettingsFeatures.getSkipSetupScreenFeatureOption()

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

        SettingsFeatures.initialize(context.applicationContext)

        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }
        val selectedCallScreenOrientation = SettingsFeatures.callScreenOrientation()
        val callScreenOrientation = selectedCallScreenOrientation?.let { SettingsFeatures.orientation(it) }
        val selectedSetupScreenOrientation = SettingsFeatures.setupScreenOrientation()
        val setupScreenOrientation = selectedSetupScreenOrientation?.let { SettingsFeatures.orientation(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(
                CallCompositeLocalizationOptions(
                    locale!!,
                    SettingsFeatures.getLayoutDirection()
                )
            )
            .localization(CallCompositeLocalizationOptions(locale, SettingsFeatures.getLayoutDirection()))
            .setupScreenOrientation(setupScreenOrientation)
            .callScreenOrientation(callScreenOrientation)

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        val callComposite = callCompositeBuilder.build()

        // For test purposes we will keep a static ref to CallComposite
        CallLauncherViewModel.callComposite = callComposite

        this.callComposite = callComposite

        subscribeToEvents(context)

        return callComposite
    }

    private fun unsubscribe() {
        callComposite?.let { composite ->
            composite.removeOnCallStateChangedEventHandler(callStateEventHandler)
            composite.removeOnErrorEventHandler(errorHandler)
            composite.removeOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
            incomingCallEvent?.let {
                composite.removeOnIncomingCallEventHandler(incomingCallEvent)
            }
            incomingCallEndEvent?.let {
                composite.removeOnIncomingCallEndEventHandler(incomingCallEndEvent)
            }
        }
    }

    fun callHangup() {
        isExitRequested = true
        callComposite?.dismiss()
    }

    companion object {
        var callComposite: CallComposite? = null
    }

    fun registerFirebaseToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(context, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show()
                    return@OnCompleteListener
                }

                val token = task.result
                val callComposite = createCallComposite(context)
                callComposite.registerPushNotification(
                    context,
                    CallCompositePushNotificationOptions(
                        CommunicationTokenCredential(BuildConfig.ACS_TOKEN),
                        token,
                        BuildConfig.USER_NAME
                    )
                )
            }
        )
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
        CallLauncherActivity.callCompositeEvents?.onCompositeDismiss()
    }
}

class IncomingCallEvent : CallCompositeEventHandler<CallCompositeIncomingCallEvent> {
    override fun handle(eventArgs: CallCompositeIncomingCallEvent) {
        Log.i(CallLauncherActivity.TAG, "Showing IncomingCallEvent")
        CallLauncherActivity.callCompositeEvents?.showIncomingCallUI(eventArgs.incomingCallInfo)
    }
}

class IncomingCallEndEvent : CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> {
    override fun handle(eventArgs: CallCompositeIncomingCallEndEvent?) {
        Log.i(CallLauncherActivity.TAG, "Dismissing IncomingCallEvent " + eventArgs?.code)
        CallLauncherActivity.callCompositeEvents?.hideIncomingCallUI()
        CallLauncherActivity.callCompositeEvents?.incomingCallEnded()
    }
}
