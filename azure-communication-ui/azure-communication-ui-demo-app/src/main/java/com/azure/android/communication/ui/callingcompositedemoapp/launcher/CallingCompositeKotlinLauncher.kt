// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallCompositeBuilder
import com.azure.android.communication.ui.GroupCallOptions
import com.azure.android.communication.ui.TeamsMeetingOptions
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getLanguageCode
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.isRTL
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.language
import com.azure.android.communication.ui.configuration.LocalizationConfiguration
import com.azure.android.communication.ui.configuration.ThemeConfiguration
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
        val selectedLanguage = language()

        val callComposite: CallComposite =
            if (AdditionalFeatures.secondaryThemeFeature.active)
                CallCompositeBuilder().theme(ThemeConfiguration(R.style.MyCompany_Theme_Calling))
                    .localization(
                        LocalizationConfiguration(
                            getLanguageCode(selectedLanguage!!),
                            isRTL()
                        )
                    )
                    .build()
            else
                CallCompositeBuilder().localization(
                    LocalizationConfiguration(
                        getLanguageCode(selectedLanguage!!),
                        isRTL()
                    )
                )
                    .build()

        callComposite.setOnErrorHandler(CallLauncherActivityErrorHandler(callLauncherActivity))

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        if (groupId != null) {
            val groupCallOptions = GroupCallOptions(
                communicationTokenCredential,
                groupId,
                displayName,
            )
            callComposite.launch(callLauncherActivity, groupCallOptions)
        } else if (!meetingLink.isNullOrBlank()) {
            val teamsMeetingOptions = TeamsMeetingOptions(
                communicationTokenCredential,
                meetingLink,
                displayName,
            )
            callComposite.launch(callLauncherActivity, teamsMeetingOptions)
        }
    }
}
