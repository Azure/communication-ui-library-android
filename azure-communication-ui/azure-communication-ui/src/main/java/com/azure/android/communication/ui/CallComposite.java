// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.configuration.CallConfiguration;
import com.azure.android.communication.ui.configuration.CallType;
import com.azure.android.communication.ui.presentation.CallCompositeActivity;

import java.util.UUID;

/**
 * Azure android communication calling composite component.
 *
 * <p><strong>Instantiating Call Composite</strong></p>
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .theme&#40;new ThemeConfiguration&#40;themeId&#41;&#41;
 *     .customizeLocalization&#40;new LocalizationConfiguration&#40;languageCode, isRightToLeft&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallCompositeBuilder
 */
public final class CallComposite {

    // Each time we launch, an InstanceID will be assigned and incremented.
    private static int instanceId = 0;

    private final CallCompositeConfiguration configuration;

    CallComposite(final CallCompositeConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Launch group call composite.
     *
     * <pre>
     *
     * final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
     *                 new CommunicationTokenRefreshOptions&#40;tokenRefresher, true&#41;;
     * final CommunicationTokenCredential communicationTokenCredential =
     *                 new CommunicationTokenCredential&#40;communicationTokenRefreshOptions&#41;;
     * final GroupCallOptions groupCallOptions =
     *                 new GroupCallOptions&#40;context, communicationTokenCredential, groupId, displayName&#41;;
     * callComposite.launch&#40;groupCallOptions&#41;;
     *
     * </pre>
     *
     * @param context          The android context used to start the Composite.
     * @param groupCallOptions The {@link GroupCallOptions} has parameters to
     *                         launch group call experience.
     */
    public void launch(final Context context, final GroupCallOptions groupCallOptions) {
        launch(
                context,
                groupCallOptions.getCredential(),
                groupCallOptions.getDisplayName(),
                groupCallOptions.getGroupId(),
                null,
                CallType.GROUP_CALL
        );
    }

    /**
     * Launch Teams meeting call composite.
     *
     * <pre>
     *
     * final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
     *                 new CommunicationTokenRefreshOptions&#40;tokenRefresher, true&#41;;
     * final CommunicationTokenCredential communicationTokenCredential =
     *                 new CommunicationTokenCredential&#40;communicationTokenRefreshOptions&#41;;
     * final TeamsMeetingOptions teamsMeetingOptions =
     *                 new TeamsMeetingOptions&#40;context, communicationTokenCredential, meetingLink, displayName&#41;;
     * callComposite.launch&#40;teamsMeetingOptions&#41;;
     *
     * </pre>
     *
     * @param context             The android context used to start the Composite.
     * @param teamsMeetingOptions The {@link TeamsMeetingOptions} has parameters to
     *                            launch Teams meeting experience.
     */
    public void launch(final Context context, final TeamsMeetingOptions teamsMeetingOptions) {
        launch(
                context,
                teamsMeetingOptions.getCredential(),
                teamsMeetingOptions.getDisplayName(),
                null,
                teamsMeetingOptions.getMeetingLink(),
                CallType.TEAMS_MEETING
        );
    }

    /**
     * Set {@link CallingEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; set error handler
     * callComposite.setOnErrorHandler&#40;eventHandler -> {
     *     &#47;&#47; Process error event
     *     System.out.println&#40;eventHandler.getCause&#40;&#41;&#41;;
     *     System.out.println&#40;eventHandler.getErrorCode&#40;&#41;&#41;;
     * }&#41;;
     *
     *
     * &#47;&#47; remove error handler
     * callComposite.setOnErrorHandler&#40;null&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallingEventHandler}.
     */
    public void setOnErrorHandler(final CallingEventHandler eventHandler) {
        configuration.getCallCompositeEventsHandler().setOnErrorHandler(eventHandler);
    }

    private void launch(
            final Context context,
            final CommunicationTokenCredential communicationTokenCredential,
            final String displayName,
            final UUID groupId,
            final String meetingLink,
            final CallType callType
    ) {
        configuration.setCallConfig(new CallConfiguration(
                communicationTokenCredential,
                displayName,
                groupId,
                meetingLink,
                callType
        ));

        CallCompositeConfiguration.Companion.putConfig(instanceId, configuration);

        final Intent intent = new Intent(context, CallCompositeActivity.class);
        intent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId++);
        context.startActivity(intent);
    }
}
