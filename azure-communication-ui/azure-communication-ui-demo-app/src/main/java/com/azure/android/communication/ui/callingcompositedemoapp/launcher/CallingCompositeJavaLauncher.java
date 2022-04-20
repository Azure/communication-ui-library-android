// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher;

import android.text.TextUtils;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.CallCompositeBuilder;
import com.azure.android.communication.ui.GroupCallOptions;
import com.azure.android.communication.ui.TeamsMeetingOptions;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityRemoteParticipantJoinedHandler;
import com.azure.android.communication.ui.callingcompositedemoapp.R;
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures;
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures;
import com.azure.android.communication.ui.configuration.LanguageCode;
import com.azure.android.communication.ui.configuration.CommunicationUILocalDataOptions;
import com.azure.android.communication.ui.configuration.LocalizationConfiguration;
import com.azure.android.communication.ui.configuration.ThemeConfiguration;
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData;

import java.util.UUID;
import java.util.concurrent.Callable;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CallingCompositeJavaLauncher implements CallingCompositeLauncher {
    private final Callable<String> tokenRefresher;

    public CallingCompositeJavaLauncher(final Callable<String> tokenRefresher) {
        this.tokenRefresher = tokenRefresher;
    }

    @Override
    public void launch(final CallLauncherActivity callLauncherActivity,
                       final String displayName,
                       final UUID groupId,
                       final String meetingLink,
                       final Function1<? super String, Unit> showAlert) {

        final CallCompositeBuilder builder = new CallCompositeBuilder();

        SettingsFeatures.initialize(callLauncherActivity.getApplicationContext());

        final CommunicationUIPersonaData personaData =
                SettingsFeatures.getCommunicationUIPersonaData(callLauncherActivity.getApplicationContext());

        final String selectedLanguage = SettingsFeatures.language();
        final LanguageCode languageCode = SettingsFeatures
                .selectedLanguageCode(SettingsFeatures.languageCode(selectedLanguage));

        builder.localization(new LocalizationConfiguration(languageCode,
                SettingsFeatures.isRTL()));

        if (AdditionalFeatures.Companion.getSecondaryThemeFeature().getActive()) {
            builder.theme(new ThemeConfiguration(R.style.MyCompany_Theme_Calling));
        }

        final CallComposite callComposite = builder.build();
        callComposite.setOnErrorHandler(new CallLauncherActivityErrorHandler(callLauncherActivity));
        callComposite.setOnRemoteParticipantJoinedHandler(
                new CallLauncherActivityRemoteParticipantJoinedHandler(callComposite)
        );

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        if (groupId != null) {
            final GroupCallOptions groupCallOptions =
                    new GroupCallOptions(communicationTokenCredential, groupId, displayName);
            if (personaData != null) {
                final CommunicationUILocalDataOptions dataOptions =
                        new CommunicationUILocalDataOptions(personaData);
                callComposite.launch(callLauncherActivity, groupCallOptions, dataOptions);
            } else {
                callComposite.launch(callLauncherActivity, groupCallOptions);
            }
        } else if (!TextUtils.isEmpty(meetingLink)) {
            final TeamsMeetingOptions teamsMeetingOptions =
                    new TeamsMeetingOptions(communicationTokenCredential, meetingLink, displayName);
            if (personaData != null) {
                final CommunicationUILocalDataOptions dataOptions =
                        new CommunicationUILocalDataOptions(personaData);
                callComposite.launch(callLauncherActivity, teamsMeetingOptions, dataOptions);
            } else {
                callComposite.launch(callLauncherActivity, teamsMeetingOptions);
            }
        }
    }
}
