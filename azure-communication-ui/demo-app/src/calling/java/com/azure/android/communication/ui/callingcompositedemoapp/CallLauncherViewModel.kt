// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions
/* <ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
/* </ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
/* <ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator
/* </ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class CallLauncherViewModel : ViewModel() {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)
    val userReportedIssueEventHandler: UserReportedIssueHandler = UserReportedIssueHandler()

    var isExitRequested = false
    private val callStateEventHandler = CallStateEventHandler(callCompositeCallStateStateFlow)
    private var exitEventHandler: CallExitEventHandler? = null
    private var errorHandler: CallLauncherActivityErrorHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedHandler? = null
    private var callComposite: CallComposite? = null

    fun launch(
        context: Context,
        acsToken: String,
        displayName: String,
        groupId: UUID?,
        /* <ROOMS_SUPPORT:5> */
        roomId: String?,
        roomRoleHint: CallCompositeParticipantRole?,
        /* </ROOMS_SUPPORT:2> */
        meetingLink: String?,
    ) {
        // The handler needs the application context to manage notifications.
        userReportedIssueEventHandler.context = context.applicationContext as Application

        val callComposite = createCallComposite(context)
        callComposite.addOnErrorEventHandler(
            CallLauncherActivityErrorHandler(
                context,
                callComposite
            )
        )

        if (SettingsFeatures.getRemoteParticipantPersonaInjectionSelection()) {
            callComposite.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(callComposite, context)
            )
        }

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

        val locator: CallCompositeJoinLocator =
            if (groupId != null) CallCompositeGroupCallLocator(groupId)
            else if (meetingLink != null) CallCompositeTeamsMeetingLinkLocator(meetingLink)
            /* <ROOMS_SUPPORT:3> */
            else if (roomId != null && roomRoleHint != null) CallCompositeRoomLocator(roomId)
            /* </ROOMS_SUPPORT:1> */
            else throw IllegalArgumentException("Cannot launch call composite with provided arguments.")

        val remoteOptions =
            CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)

        val avMode = if (SettingsFeatures.getAudioOnlyByDefaultOption())
            CallCompositeAudioVideoMode.AUDIO_ONLY else CallCompositeAudioVideoMode.AUDIO_AND_VIDEO

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            /* <ROOMS_SUPPORT:7> */
            .setRoleHint(roomRoleHint)
            /* </ROOMS_SUPPORT:4> */
            .setSkipSetupScreen(SettingsFeatures.getSkipSetupScreenFeatureOption())
            .setAudioVideoMode(avMode)
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callCompositeExitSuccessStateFlow.value = false
        exitEventHandler = CallExitEventHandler(
            callCompositeExitSuccessStateFlow,
            callCompositeCallStateStateFlow,
            this
        )
        subscribeToEvents(context)
        isExitRequested = false

        callComposite.launch(context, remoteOptions, localOptions)
    }

    private fun subscribeToEvents(context: Context) {
        callComposite?.apply {
            errorHandler = CallLauncherActivityErrorHandler(
                context, this
            )
            addOnErrorEventHandler(errorHandler)

            remoteParticipantJoinedEvent = RemoteParticipantJoinedHandler(callComposite!!, context)
            callComposite?.addOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)

            exitEventHandler = CallExitEventHandler(
                callCompositeExitSuccessStateFlow,
                callCompositeCallStateStateFlow,
                this@CallLauncherViewModel
            )
            addOnCallStateChangedEventHandler(callStateEventHandler)
            addOnDismissedEventHandler(exitEventHandler)
            addOnUserReportedEventHandler(userReportedIssueEventHandler)
        }
    }

    private fun unsubscribeFromEvents() {
        callComposite?.apply {
            removeOnCallStateChangedEventHandler(callStateEventHandler)
            removeOnErrorEventHandler(errorHandler)
            removeOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)
            removeOnDismissedEventHandler(exitEventHandler)
            removeOnUserReportedEventHandler(userReportedIssueEventHandler)
        }
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

    private fun createCallComposite(context: Context): CallComposite {
        SettingsFeatures.initialize(context.applicationContext)

        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }
        val selectedCallScreenOrientation = SettingsFeatures.callScreenOrientation()
        val callScreenOrientation =
            selectedCallScreenOrientation?.let { SettingsFeatures.orientation(it) }
        val selectedSetupScreenOrientation = SettingsFeatures.setupScreenOrientation()
        val setupScreenOrientation =
            selectedSetupScreenOrientation?.let { SettingsFeatures.orientation(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(
                CallCompositeLocalizationOptions(
                    locale!!,
                    SettingsFeatures.getLayoutDirection()
                )
            )
            .localization(
                CallCompositeLocalizationOptions(
                    locale,
                    SettingsFeatures.getLayoutDirection()
                )
            )
            .setupScreenOrientation(setupScreenOrientation)
            .callScreenOrientation(callScreenOrientation)

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        callCompositeBuilder.multitasking(
            CallCompositeMultitaskingOptions(
                SettingsFeatures.enableMultitasking(),
                SettingsFeatures.enablePipWhenMultitasking()
            )
        )

        val newCallComposite = callCompositeBuilder.build()

        callComposite = newCallComposite
        return newCallComposite
    }

    fun displayCallCompositeIfWasHidden(context: Context) {
        callComposite?.bringToForeground(context)
    }

    fun unsubscribe() {
        callComposite?.let { composite ->
            composite.removeOnCallStateChangedEventHandler(callStateEventHandler)
            exitEventHandler?.let {
                composite.removeOnDismissedEventHandler(exitEventHandler)
            }
        }
    }

    fun callHangup() {
        callComposite?.apply {
            callHangup()
        }
    }
}

class CallStateEventHandler(private val callCompositeCallStateStateFlow: MutableStateFlow<String>) :
    CallCompositeEventHandler<CallCompositeCallStateChangedEvent> {
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
    }
}
