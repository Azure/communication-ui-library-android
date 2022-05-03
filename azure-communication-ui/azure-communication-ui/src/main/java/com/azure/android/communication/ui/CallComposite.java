// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.configuration.CallConfiguration;
import com.azure.android.communication.ui.configuration.CallType;
import com.azure.android.communication.ui.configuration.LocalDataOptions;
import com.azure.android.communication.ui.configuration.events.CommunicationUIErrorEvent;
import com.azure.android.communication.ui.configuration.events.CommunicationUIRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.persona.PersonaData;
import com.azure.android.communication.ui.persona.SetPersonaDataResult;
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
 *     .localization&#40;new LocalizationConfiguration&#40;Locale.CHINESE, LayoutDirection.RTL&#41;&#41;;
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
     * final LocalDataOptions dataOptions =
     *                 new LocalDataOptions&#40;personaData&#41;;
     * callComposite.launch&#40;groupCallOptions, dataOptions&#41;;
     *
     * </pre>
     *
     * @param context                         The android context used to start the Composite.
     * @param groupCallOptions                The {@link GroupCallOptions} has parameters to
     *                                        launch group call experience.
     * @param localDataOptions The {@link LocalDataOptions} has parameters to
     *                                        launch group call experience.
     */
    public void launch(final Context context,
                       final GroupCallOptions groupCallOptions,
                       final LocalDataOptions localDataOptions) {
        launch(
                context,
                groupCallOptions.getCredential(),
                groupCallOptions.getDisplayName(),
                groupCallOptions.getGroupId(),
                null,
                CallType.GROUP_CALL,
                localDataOptions
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
     * final LocalDataOptions dataOptions =
     *                 new LocalDataOptions&#40;personaData&#41;;
     * callComposite.launch&#40;teamsMeetingOptions, dataOptions&#41;;
     *
     * </pre>
     *
     * @param context                         The android context used to start the Composite.
     * @param teamsMeetingOptions             The {@link TeamsMeetingOptions} has parameters to
     *                                        launch Teams meeting experience.
     * @param localDataOptions The {@link LocalDataOptions} has parameters to
     *                                        launch group call experience.
     */
    public void launch(final Context context,
                       final TeamsMeetingOptions teamsMeetingOptions,
                       final LocalDataOptions localDataOptions) {
        launch(
                context,
                teamsMeetingOptions.getCredential(),
                teamsMeetingOptions.getDisplayName(),
                null,
                teamsMeetingOptions.getMeetingLink(),
                CallType.TEAMS_MEETING,
                localDataOptions
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
    public void setOnErrorHandler(final CallingEventHandler<CommunicationUIErrorEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().setOnErrorHandler(eventHandler);
    }

    /**
     * Set {@link CallingEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; set remote participant joined handler
     * callComposite.setOnRemoteParticipantJoinedHandler&#40;eventHandler -> {
     *     &#47;&#47; Use call composite to set configurations for remote participant
     * }&#41;;
     *
     *
     * &#47;&#47; remove remote participant joined handler
     * callComposite.setOnRemoteParticipantJoinedHandler&#40;null&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallingEventHandler}.
     */
    public void setOnRemoteParticipantJoinedHandler(
            final CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().setOnRemoteParticipantJoinedHandler(eventHandler);
    }

    /**
     * Set {@link PersonaData}.
     *
     * @param identifier  The {@link CommunicationIdentifier}.
     * @param personaData The {@link PersonaData}.
     * @return {@link SetPersonaDataResult}
     */
    public SetPersonaDataResult setRemoteParticipantPersonaData(@NonNull final CommunicationIdentifier identifier,
                                                                @NonNull final PersonaData personaData) {
        return configuration.getRemoteParticipantsConfiguration().setPersonaData(identifier, personaData);
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

    private void launch(
            final Context context,
            final CommunicationTokenCredential communicationTokenCredential,
            final String displayName,
            final UUID groupId,
            final String meetingLink,
            final CallType callType,
            final LocalDataOptions localDataOptions
    ) {
        configuration.setCallConfig(new CallConfiguration(
                communicationTokenCredential,
                displayName,
                groupId,
                meetingLink,
                callType
        ));

        configuration.setLocalDataOptions(localDataOptions);

        CallCompositeConfiguration.Companion.putConfig(instanceId, configuration);

        final Intent intent = new Intent(context, CallCompositeActivity.class);
        intent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId++);
        context.startActivity(intent);
    }
}
