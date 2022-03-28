// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.CallCompositeBuilder;
import com.azure.android.communication.ui.GroupCallOptions;
import com.azure.android.communication.ui.TeamsMeetingOptions;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity;
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler;
import com.azure.android.communication.ui.callingcompositedemoapp.R;
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures;
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures;
import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration;
import com.azure.android.communication.ui.configuration.LocalizationConfiguration;
import com.azure.android.communication.ui.configuration.ThemeConfiguration;
import com.azure.android.communication.ui.persona.PersonaData;

import java.io.IOException;
import java.net.URL;
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
        final String selectedLanguage = SettingsFeatures.Companion.language(callLauncherActivity
                .getApplicationContext());

        if (SettingsFeatures.Companion.getIsCustomTranslationEnabled(
                callLauncherActivity.getApplicationContext())) {
            builder.customizeLocalization(new LocalizationConfiguration(SettingsFeatures.Companion
                    .getLanguageCode(selectedLanguage),
                    SettingsFeatures.Companion.isRTL(callLauncherActivity
                            .getApplicationContext()),
                    SettingsFeatures.Companion.getCustomTranslationMap()));
        } else {
            builder.customizeLocalization(new LocalizationConfiguration(SettingsFeatures.Companion
                    .getLanguageCode(selectedLanguage),
                    SettingsFeatures.Companion.isRTL(callLauncherActivity.getApplicationContext())));
        }

        if (AdditionalFeatures.Companion.getSecondaryThemeFeature().getActive()) {
            builder.theme(new ThemeConfiguration(R.style.MyCompany_Theme_Calling));
        }

        final CallComposite callComposite = builder.build();
        callComposite.setOnErrorHandler(new CallLauncherActivityErrorHandler(callLauncherActivity));

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        try {
            final URL url = new URL(
                    "https://bestbuyerpersona.com/wp-content/uploads/2022/02/undraw_profile_pic_ic5t.png"
            );
            final Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            final PersonaData personaData = new PersonaData(imageBitmap);
            final LocalParticipantConfiguration configuration = new LocalParticipantConfiguration(personaData);
            if (groupId != null) {
                final GroupCallOptions groupCallOptions =
                        new GroupCallOptions(communicationTokenCredential, groupId, displayName);

                callComposite.launch(callLauncherActivity, groupCallOptions, configuration);

            } else if (!TextUtils.isEmpty(meetingLink)) {
                final TeamsMeetingOptions teamsMeetingOptions =
                        new TeamsMeetingOptions(communicationTokenCredential, meetingLink, displayName);

                callComposite.launch(callLauncherActivity, teamsMeetingOptions, configuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
