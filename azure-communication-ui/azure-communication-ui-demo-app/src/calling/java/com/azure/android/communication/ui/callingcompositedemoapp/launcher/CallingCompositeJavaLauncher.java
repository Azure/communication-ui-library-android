// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher;

import android.text.TextUtils;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.calling.CallComposite;
import com.azure.android.communication.ui.calling.CallCompositeBuilder;
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingOptions;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler;
import com.azure.android.communication.ui.callingcompositedemoapp.R;
import com.azure.android.communication.ui.callingcompositedemoapp.RemoteParticipantJoinedHandler;
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures;
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures;
import com.azure.android.communication.ui.calling.models.CallCompositeClientOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;

import java.util.Locale;
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

        final CallCompositeParticipantViewData participantViewData =
                SettingsFeatures.getParticipantViewData(callLauncherActivity.getApplicationContext());

        final String selectedLanguage = SettingsFeatures.language();
        final Locale locale = SettingsFeatures.locale(selectedLanguage);

        builder.localization(new CallCompositeLocalizationOptions(locale,
                SettingsFeatures.getLayoutDirection()));

        if (AdditionalFeatures.Companion.getSecondaryThemeFeature().getActive()) {
            builder.theme(R.style.MyCompany_Theme_Calling);
        }

        final CallComposite callComposite = builder.build();
        callComposite.setOnErrorHandler(new CallLauncherActivityErrorHandler(callLauncherActivity));

        if (SettingsFeatures.getRemoteParticipantPersonaInjectionSelection()) {
            callComposite.setOnRemoteParticipantJoinedHandler(
                    new RemoteParticipantJoinedHandler(callComposite, callLauncherActivity));
        }

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        if (groupId != null) {
            final CallCompositeGroupCallOptions groupCallOptions =
                    new CallCompositeGroupCallOptions(communicationTokenCredential, groupId, displayName);
            if (participantViewData != null) {
                final CallCompositeClientOptions dataOptions =
                        new CallCompositeClientOptions(participantViewData);
                callComposite.launch(callLauncherActivity, groupCallOptions, dataOptions);
            } else {
                callComposite.launch(callLauncherActivity, groupCallOptions);
            }
        } else if (!TextUtils.isEmpty(meetingLink)) {
            final CallCompositeTeamsMeetingOptions teamsMeetingOptions =
                    new CallCompositeTeamsMeetingOptions(communicationTokenCredential, meetingLink, displayName);
            if (participantViewData != null) {
                final CallCompositeClientOptions dataOptions =
                        new CallCompositeClientOptions(participantViewData);
                callComposite.launch(callLauncherActivity, teamsMeetingOptions, dataOptions);
            } else {
                callComposite.launch(callLauncherActivity, teamsMeetingOptions);
            }
        }
    }
}
