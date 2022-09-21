// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp.launcher

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeNavigationBarViewData
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callwithchatdemoapp.CallLauncherActivityErrorHandler
import com.azure.android.communication.ui.callwithchatdemoapp.CallWithChatLauncherActivity
import com.azure.android.communication.ui.callwithchatdemoapp.RemoteParticipantJoinedHandler
import com.azure.android.communication.ui.callwithchatdemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getLayoutDirection
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getParticipantViewData
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getRemoteParticipantPersonaInjectionSelection
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getSubtitle
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getTitle
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.initialize
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.language
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.locale
import java.util.UUID
import java.util.concurrent.Callable

class CallWithChatCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    CallWithChatCompositeLauncher {

    override fun launch(
        callLauncherActivity: CallWithChatLauncherActivity,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        showAlert: ((String) -> Unit)?,
    ) {
        initialize(callLauncherActivity.applicationContext)
        val selectedLanguage = language()
        val locale = selectedLanguage?.let { locale(it) }

        val callComposite: CallComposite =
            if (AdditionalFeatures.secondaryThemeFeature.active)
                CallCompositeBuilder()
                    .localization(CallCompositeLocalizationOptions(locale!!, getLayoutDirection()))
                    .build()
            else
                CallCompositeBuilder()
                    .localization(CallCompositeLocalizationOptions(locale!!, getLayoutDirection()))
                    .build()

        callComposite.addOnErrorEventHandler(CallLauncherActivityErrorHandler(callLauncherActivity))

        if (getRemoteParticipantPersonaInjectionSelection()) {
            callComposite.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(callComposite, callLauncherActivity)
            )
        }

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallCompositeJoinLocator =
            if (groupId != null) CallCompositeGroupCallLocator(groupId)
            else CallCompositeTeamsMeetingLinkLocator(meetingLink)

        val remoteOptions =
            CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(getParticipantViewData(callLauncherActivity.applicationContext))
            .setNavigationBarViewData(
                CallCompositeNavigationBarViewData(getTitle())
                    .setSubtitle(getSubtitle())
            )

        callComposite.launch(callLauncherActivity, remoteOptions, localOptions)
    }
}
