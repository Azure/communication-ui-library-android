// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp.launcher

import android.content.Context
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.callwithchat.CallWithChatComposite
import com.azure.android.communication.ui.callwithchat.CallWithChatCompositeBuilder
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeCallAndChatLocator
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeJoinLocator
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalizationOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callwithchatdemoapp.AlertHandler
import com.azure.android.communication.ui.callwithchatdemoapp.CallWithChatLauncherActivityErrorHandler
import com.azure.android.communication.ui.callwithchatdemoapp.RemoteParticipantJoinedHandler
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getLayoutDirection
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getParticipantViewData
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.getRemoteParticipantPersonaInjectionSelection
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.initialize
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.language
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures.Companion.locale
import com.azure.android.communication.ui.demoapp.AuthService
import java.util.UUID

internal class CallWithChatCompositeKotlinLauncher :
    CallWithChatCompositeLauncher {

    override fun launch(
        context: Context,
        alertHandler: AlertHandler,
        authService: AuthService,
        displayName: String,
        acsEndpoint: String,
        groupId: UUID?,
        chatThreadId: String?,
        meetingLink: String?,
    ) {
        initialize(context)
        val selectedLanguage = language()
        val locale = selectedLanguage?.let { locale(it) }

        val builder = CallWithChatCompositeBuilder()
            .localization(CallWithChatCompositeLocalizationOptions(locale!!, getLayoutDirection()))

        val composite: CallWithChatComposite = builder.build()

        composite.addOnErrorEventHandler(CallWithChatLauncherActivityErrorHandler(alertHandler))

        if (getRemoteParticipantPersonaInjectionSelection()) {
            composite.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(composite, context)
            )
        }

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ authService.tokenRefresher() }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallWithChatCompositeJoinLocator =
            if (groupId != null) CallWithChatCompositeCallAndChatLocator(
                acsEndpoint,
                groupId,
                chatThreadId
            )
            else CallWithChatCompositeTeamsMeetingLinkLocator(acsEndpoint, meetingLink)

        val remoteOptions = CallWithChatCompositeRemoteOptions(
            locator,
            authService.currentUserCommunicationIdentifier,
            communicationTokenCredential,
            displayName
        )

        val localOptions = CallWithChatCompositeLocalOptions()
            .setParticipantViewData(getParticipantViewData(context))

        composite.launch(context, remoteOptions, localOptions)
    }
}
