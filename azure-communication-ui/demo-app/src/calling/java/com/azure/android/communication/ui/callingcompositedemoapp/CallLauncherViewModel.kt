// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CallLauncherViewModel : ViewModel() {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    val callCompositeExitSuccessStateFlow = MutableStateFlow(false)
    val userReportedIssueEventHandler: UserReportedIssueHandler = UserReportedIssueHandler()

    private var callStateEventHandler: ((CallCompositeCallStateChangedEvent) -> Unit)? = null
    private var onDismissed: ((CallCompositeDismissedEvent) -> Unit)? = null
    private var errorHandler: CallLauncherActivityErrorHandler? = null
    private var remoteParticipantJoinedEvent: RemoteParticipantJoinedHandler? = null
    private var callComposite: CallComposite? = null

    fun launch(
        context: Context,
        acsToken: String,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
    ) {
        // The handler needs the application context to manage notifications.
        userReportedIssueEventHandler.context = context.applicationContext as Application
        if (SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(context).show(this)
        } else {
            EndCompositeButtonView.get(context).hide()
        }
        callCompositeExitSuccessStateFlow.value = false

        val remoteOptions = getRemoteOptions(acsToken, groupId, meetingLink, displayName)
        val localOptions = getLocalOptions(context)

        val callComposite = createCallComposite()
        subscribeToEvents(context, callComposite)

        this.callComposite = callComposite
        if (localOptions == null)
            callComposite.launch(context, remoteOptions)
        else
            callComposite.launch(context, remoteOptions, localOptions)
    }

    fun dismissCallComposite() {
        callComposite?.dismiss()
    }

    fun bringCallCompositeToForeground(context: Context) {
        callComposite?.bringToForeground(context)
    }

    private fun getRemoteOptions(
        acsToken: String,
        groupId: UUID?,
        meetingLink: String?,
        displayName: String
    ): CallCompositeRemoteOptions {
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallCompositeJoinLocator =
            if (groupId != null) CallCompositeGroupCallLocator(groupId)
            else CallCompositeTeamsMeetingLinkLocator(meetingLink)

        return CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)
    }

    private fun getLocalOptions(context: Context): CallCompositeLocalOptions? {
        val localOptions = CallCompositeLocalOptions()
        var isAnythingChanged = false

        if (SettingsFeatures.getParticipantViewData(context.applicationContext) != null){
            localOptions.setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            isAnythingChanged = true
        }
        SettingsFeatures.getTitle()?.let { title ->
            val setupScreenViewData = CallCompositeSetupScreenViewData().setTitle(title)
            SettingsFeatures.getSubtitle()?.let { subTitle ->
                setupScreenViewData.setSubtitle(subTitle)
            }

            localOptions.setSetupScreenViewData(setupScreenViewData)
            isAnythingChanged = true
        }
        SettingsFeatures.getSkipSetupScreenFeatureOption()?.let {
            localOptions.setSkipSetupScreen(it)
            isAnythingChanged = true
        }
        SettingsFeatures.getAudioOnlyByDefaultOption()?.let {
            localOptions.setAudioVideoMode(
                if (it) CallCompositeAudioVideoMode.AUDIO_ONLY
                else CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
            )
            isAnythingChanged = true
        }
        SettingsFeatures.getCameraOnByDefaultOption()?.let {
            localOptions.setCameraOn(it)
            isAnythingChanged = true
        }
        SettingsFeatures.getMicOnByDefaultOption()?.let {
            localOptions.setMicrophoneOn(it)
            isAnythingChanged = true
        }

        return if (isAnythingChanged) localOptions else null
    }

    private fun subscribeToEvents(context: Context, callComposite: CallComposite) {

        errorHandler = CallLauncherActivityErrorHandler(context, callComposite)
        callComposite.addOnErrorEventHandler(errorHandler)

        remoteParticipantJoinedEvent = RemoteParticipantJoinedHandler(callComposite, context)
        callComposite.addOnRemoteParticipantJoinedEventHandler(remoteParticipantJoinedEvent)

        callStateEventHandler = {
            callCompositeCallStateStateFlow.value = it.code.toString()
            toast(context, "Call State: ${it.code}.")
        }

        callComposite.addOnCallStateChangedEventHandler(callStateEventHandler)
        callComposite.addOnUserReportedEventHandler(userReportedIssueEventHandler)

        onDismissed = {
            toast(context, "onDismissed: errorCode: ${it.errorCode}, cause: ${it.cause?.message}.")
            this.callComposite = null
        }
        callComposite.addOnDismissedEventHandler(onDismissed)

        callComposite.addOnPictureInPictureChangedEventHandler {
            toast(context, "isInPictureInPicture: " + it.isInPictureInPicture)
        }
    }

    private fun displayDebugInfoIn20Seconds(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(20000)

            callComposite?.getDebugInfo(context)?.let {
                val result = """Calling UI Version: ${it.versions.azureCallingUILibrary}
                        Calling SDK Version: ${it.versions.azureCallingLibrary}                    
                        Call History (${it.callHistoryRecords.size}) 
                        Log Files (${it.logFiles.size})"""
                result.split("\n").map { line -> line.trim() }.forEach {
                    toast(context, it)
                }
            }
        }
    }

    private fun toast(context:Context, message: String) {
        Log.i("ACSCallingUI", message)
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun getCallHistory(context: Context): List<CallCompositeCallHistoryRecord> {
        val callComposite = this.callComposite ?: createCallComposite()

        return callComposite.getDebugInfo(context).callHistoryRecords
    }

    private fun createCallComposite(): CallComposite {
        val locale = SettingsFeatures.locale(SettingsFeatures.language())
        val callScreenOrientation = SettingsFeatures.orientation(SettingsFeatures.callScreenOrientation())
        val setupScreenOrientation = SettingsFeatures.orientation(SettingsFeatures.setupScreenOrientation())

        val callCompositeBuilder = CallCompositeBuilder()

        if (setupScreenOrientation != null)
            callCompositeBuilder.setupScreenOrientation(setupScreenOrientation)

        if (callScreenOrientation != null)
            callCompositeBuilder.callScreenOrientation(callScreenOrientation)

        locale?.let {
            val isRtl = SettingsFeatures.getLayoutDirection()
            callCompositeBuilder
                .localization(
                    if (isRtl != null)
                        CallCompositeLocalizationOptions(it, isRtl)
                    else
                        CallCompositeLocalizationOptions(it)
                )
        }

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        if (SettingsFeatures.enableMultitasking() != null) {
            val multitaskingOptions = if (SettingsFeatures.enablePipWhenMultitasking() != null)
                CallCompositeMultitaskingOptions(
                    SettingsFeatures.enableMultitasking(),
                    SettingsFeatures.enablePipWhenMultitasking()
                )
            else
                CallCompositeMultitaskingOptions(
                    SettingsFeatures.enableMultitasking()
                )

            callCompositeBuilder.multitasking(multitaskingOptions)
        }

        return callCompositeBuilder.build()
    }
}
