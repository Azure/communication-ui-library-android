// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher;

import android.content.Context;
import android.text.TextUtils;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.CallCompositeBuilder;
import com.azure.android.communication.ui.GroupCallOptions;
import com.azure.android.communication.ui.TeamsMeetingOptions;

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
    public void launch(final Context context,
                       final String displayName,
                       final UUID groupId,
                       final String meetingLink,
                       final Function1<? super String, Unit> showAlert) {
        final CallComposite callComposite =
                new CallCompositeBuilder()
//                        .theme(new ThemeConfiguration(R.style.MyCompany_Theme))
                        .build();

        callComposite.setOnErrorHandler(eventHandler -> {
            System.out.println("================= application is logging exception =================");
            System.out.println(eventHandler.getCause());
            System.out.println(eventHandler.getErrorCode());
            if (eventHandler.getCause() != null) {
                showAlert.invoke(eventHandler.getErrorCode().toString() + " "
                        + eventHandler.getCause().getMessage());
            } else {
                showAlert.invoke(eventHandler.getErrorCode().toString());
            }
            System.out.println("====================================================================");
        });

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        if (groupId != null) {
            final GroupCallOptions groupCallOptions =
                    new GroupCallOptions(context, communicationTokenCredential, groupId, displayName);

            callComposite.launch(groupCallOptions);

        } else if (!TextUtils.isEmpty(meetingLink)) {
            final TeamsMeetingOptions teamsMeetingOptions =
                    new TeamsMeetingOptions(context, communicationTokenCredential, meetingLink, displayName);

            callComposite.launch(teamsMeetingOptions);
        }
    }
}
