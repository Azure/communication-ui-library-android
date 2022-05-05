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
import com.azure.android.communication.ui.callingcompositedemoapp.RemoteParticipantJoinedHandler
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getLayoutDirection
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getPersonaData
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.getRemoteParticipantPersonaInjectionSelection
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.initialize
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.language
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures.Companion.locale
import com.azure.android.communication.ui.configuration.LocalDataOptions
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
        initialize(callLauncherActivity.applicationContext)
        val personaData = getPersonaData(callLauncherActivity.applicationContext)
        val selectedLanguage = language()
        val locale = selectedLanguage?.let { locale(it) }

        val callComposite: CallComposite =
            if (AdditionalFeatures.secondaryThemeFeature.active)
                CallCompositeBuilder().theme(ThemeConfiguration(R.style.MyCompany_Theme_Calling))
                    .localization(
                        LocalizationConfiguration(
                            locale!!,
                            getLayoutDirection()
                        )
                    ).build()
            else
                CallCompositeBuilder().localization(
                    LocalizationConfiguration(
                        locale!!,
                        getLayoutDirection()
                    )
                ).build()

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

        if (groupId != null) {
            val groupCallOptions = GroupCallOptions(
                communicationTokenCredential,
                groupId,
                displayName,
            )

            if (personaData != null) {
                val dataOptions =
                    LocalDataOptions(
                        personaData
                    )
                callComposite.launch(
                    callLauncherActivity,
                    groupCallOptions,
                    dataOptions
                )
            } else {
                callComposite.launch(
                    callLauncherActivity,
                    groupCallOptions,
                )
            }
        } else if (!meetingLink.isNullOrBlank()) {
            val teamsMeetingOptions = TeamsMeetingOptions(
                communicationTokenCredential,
                meetingLink,
                displayName,
            )

            if (personaData != null) {
                val dataOptions =
                    LocalDataOptions(
                        personaData
                    )
                callComposite.launch(
                    callLauncherActivity,
                    teamsMeetingOptions,
                    dataOptions
                )
            } else {
                callComposite.launch(
                    callLauncherActivity,
                    teamsMeetingOptions,
                )
            }
        }
    }
}
