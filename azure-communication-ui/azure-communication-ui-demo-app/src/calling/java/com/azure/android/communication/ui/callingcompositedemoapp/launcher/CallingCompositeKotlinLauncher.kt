// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinMeetingLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.RemoteParticipantJoinedHandler
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getLayoutDirection
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getParticipantViewData
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getRemoteParticipantPersonaInjectionSelection
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.initialize
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.language
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.locale
import java.util.UUID
import java.util.concurrent.Callable

class CallingCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    CallingCompositeLauncher {

    override fun launch(
        callLauncherActivity: CallLauncherActivity,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        showAlert: ((String) -> Unit)?,
    ) {
        initialize(callLauncherActivity.applicationContext)
        val participantViewData = getParticipantViewData(callLauncherActivity.applicationContext)
        val selectedLanguage = language()
        val locale = selectedLanguage?.let { locale(it) }

        val callComposite: CallComposite =
            if (AdditionalFeatures.secondaryThemeFeature.active)
                CallCompositeBuilder().theme(R.style.MyCompany_Theme_Calling)
                    .localization(CallCompositeLocalizationOptions(locale!!, getLayoutDirection()))
                    .build()
            else
                CallCompositeBuilder()
                    .localization(CallCompositeLocalizationOptions(locale!!, getLayoutDirection()))
                    .build()

        callComposite.setOnErrorHandler(CallLauncherActivityErrorHandler(callLauncherActivity))

        if (getRemoteParticipantPersonaInjectionSelection()) {
            callComposite.setOnRemoteParticipantJoinedHandler(
                RemoteParticipantJoinedHandler(callComposite, callLauncherActivity)
            )
        }

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallCompositeJoinMeetingLocator =
            if (groupId != null) CallCompositeGroupCallLocator(groupId)
            else CallCompositeTeamsMeetingLinkLocator(meetingLink)

        val remoteOptions = CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)

        if (participantViewData != null) {
            val dataOptions = CallCompositeLocalOptions(participantViewData)
            callComposite.launch(callLauncherActivity, remoteOptions, dataOptions)
        } else {
            callComposite.launch(callLauncherActivity, remoteOptions)
        }
    }
}
