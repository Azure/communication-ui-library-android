// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
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
    private val userReportedIssueEventHandler = UserReportedIssueEventHandler()

    fun launch(
        context: Context,
        acsToken: String,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
    ) {
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
            else CallCompositeTeamsMeetingLinkLocator(meetingLink)

        val remoteOptions =
            CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(SettingsFeatures.getSkipSetupScreenFeatureOption())
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callCompositeExitSuccessStateFlow.value = false
        exitEventHandler = CallExitEventHandler(callCompositeExitSuccessStateFlow, callCompositeCallStateStateFlow, this)
        callComposite.addOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite.addOnDismissedEventHandler(exitEventHandler)
        callComposite.addOnUserReportedEventHandler(userReportedIssueEventHandler)
        isExitRequested = false
        callComposite.launch(context, remoteOptions, localOptions)
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
        return callComposite
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
        isExitRequested = true
        callComposite?.dismiss()
    }

    companion object {
        var callComposite: CallComposite? = null
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
    }
}

class UserReportedIssueEventHandler : CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    override fun handle(event: CallCompositeUserReportedIssueEvent) {
        print(event.toString())
    }
}