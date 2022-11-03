// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp.launcher;

import android.content.Context;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.callwithchat.CallWithChatComposite;
import com.azure.android.communication.ui.callwithchat.CallWithChatCompositeBuilder;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeCallAndChatLocator;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeJoinLocator;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalOptions;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalizationOptions;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.callwithchatdemoapp.AlertHandler;
import com.azure.android.communication.ui.callwithchatdemoapp.CallWithChatLauncherActivityErrorHandler;
import com.azure.android.communication.ui.callwithchatdemoapp.RemoteParticipantJoinedHandler;
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures;
import com.azure.android.communication.ui.demoapp.AuthService;

import java.util.Locale;
import java.util.UUID;


public class CallWithChatCompositeJavaLauncher implements CallWithChatCompositeLauncher {

    @Override
    public void launch(final Context context,
                       final AlertHandler alertHandler,
                       final AuthService authService,
                       final String displayName,
                       final String acsEndpoint,
                       final UUID groupId,
                       final String chatThreadId,
                       final String meetingLink) {

        final CallWithChatCompositeBuilder builder = new CallWithChatCompositeBuilder();

        SettingsFeatures.initialize(context);

        final String selectedLanguage = SettingsFeatures.language();
        final Locale locale = SettingsFeatures.locale(selectedLanguage);

        builder.localization(new CallWithChatCompositeLocalizationOptions(locale,
                SettingsFeatures.getLayoutDirection()));


        final CallWithChatComposite composite = builder.build();
        composite.addOnErrorEventHandler(new CallWithChatLauncherActivityErrorHandler(alertHandler));

        if (SettingsFeatures.getRemoteParticipantPersonaInjectionSelection()) {
            composite.addOnRemoteParticipantJoinedEventHandler(
                    new RemoteParticipantJoinedHandler(composite, context));
        }

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(authService::tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        final CallWithChatCompositeJoinLocator locator = groupId != null
                ? new CallWithChatCompositeCallAndChatLocator(acsEndpoint, groupId, chatThreadId)
                : new CallWithChatCompositeTeamsMeetingLinkLocator(acsEndpoint, meetingLink);

        final CallWithChatCompositeRemoteOptions remoteOptions =
                new CallWithChatCompositeRemoteOptions(
                        locator,
                        authService.getCurrentUserCommunicationIdentifier(),
                        communicationTokenCredential,
                        displayName);


        final CallWithChatCompositeLocalOptions localOptions = new CallWithChatCompositeLocalOptions()
                .setParticipantViewData(
                        SettingsFeatures.getParticipantViewData(context));

        composite.launch(context, remoteOptions, localOptions);
    }
}
