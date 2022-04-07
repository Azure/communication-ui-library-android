// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import android.graphics.Bitmap
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallCompositeBuilder
import com.azure.android.communication.ui.GroupCallOptions
import com.azure.android.communication.ui.TeamsMeetingOptions
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityRemoteParticipantJoinedHandler
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.initialize
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.isRTL
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.language
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.languageCode
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.selectedLanguageCode
import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration
import com.azure.android.communication.ui.configuration.LocalizationConfiguration
import com.azure.android.communication.ui.configuration.ThemeConfiguration
import com.azure.android.communication.ui.persona.PersonaData
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
        localParticipantAvatarBitMap: Bitmap?,
    ) {
        initialize(callLauncherActivity.applicationContext)
        val selectedLanguage = language()
        val selectedLanguageCode = selectedLanguage?.let { it ->
            languageCode(it)?.let { selectedLanguageCode(it) }
        }

        val callComposite: CallComposite =
            if (AdditionalFeatures.secondaryThemeFeature.active)
                CallCompositeBuilder().theme(ThemeConfiguration(R.style.MyCompany_Theme_Calling))
                    .localization(
                        LocalizationConfiguration(
                            selectedLanguageCode,
                            isRTL()
                        )
                    )
                    .build()
            else
                CallCompositeBuilder().localization(
                    LocalizationConfiguration(
                        selectedLanguageCode,
                        isRTL()
                    )
                )
                    .build()

        callComposite.setOnErrorHandler(CallLauncherActivityErrorHandler(callLauncherActivity))
        callComposite.setOnRemoteParticipantJoinedHandler(
            CallLauncherActivityRemoteParticipantJoinedHandler(callLauncherActivity))

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
            val personaData = PersonaData(localParticipantAvatarBitMap)
            val localParticipantConfiguration = LocalParticipantConfiguration(personaData)
            callComposite.launch(
                callLauncherActivity,
                groupCallOptions,
                localParticipantConfiguration
            )
        } else if (!meetingLink.isNullOrBlank()) {
            val teamsMeetingOptions = TeamsMeetingOptions(
                communicationTokenCredential,
                meetingLink,
                displayName,
            )
            val personaData = PersonaData(localParticipantAvatarBitMap)
            val localParticipantConfiguration = LocalParticipantConfiguration(personaData)
            callComposite.launch(
                callLauncherActivity,
                teamsMeetingOptions,
                localParticipantConfiguration
            )
        }
    }
}
