// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import android.content.Context;
import android.content.Intent;


import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallType;
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeJoinMeetingLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity;

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
 *     .theme&#40;themeId&#41;
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
     * final CommunicationTokenCredential credential =
     *                 new CommunicationTokenCredential&#40;communicationTokenRefreshOptions&#41;;
     * final CallCompositeGroupCallOptions groupCallOptions =
     *                 new CallCompositeGroupCallOptions&#40;context, credential, groupId, displayName&#41;;
     * callComposite.launch&#40;groupCallOptions&#41;;
     *
     * </pre>
     *
     * @param context          The android context used to start the Composite.
     * @param remoteOptions    The {@link CallCompositeRemoteOptions} has remote parameters to
     *                         launch call experience.
     */
    public void launch(final Context context, final CallCompositeRemoteOptions remoteOptions) {
        launch(context, remoteOptions, null);
    }

    /**
     * Launch group call composite.
     *
     * <pre>
     *
     * final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
     *                 new CommunicationTokenRefreshOptions&#40;tokenRefresher, true&#41;;
     * final CommunicationTokenCredential credential =
     *                 new CommunicationTokenCredential&#40;communicationTokenRefreshOptions&#41;;
     * final CallCompositeGroupCallOptions groupCallOptions =
     *                 new CallCompositeGroupCallOptions&#40;context, credential, groupId, displayName&#41;;
     * final CallCompositeLocalOptions localOptions =
     *                 new CallCompositeLocalOptions&#40;participantViewData&#41;;
     * callComposite.launch&#40;groupCallOptions, localOptions&#41;;
     *
     * </pre>
     *
     * @param context           The android context used to start the Composite.
     * @param remoteOptions     The {@link CallCompositeRemoteOptions} has remote parameters to
     *                              launch group call experience.
     * @param localOptions      The {@link CallCompositeLocalOptions} has local parameters to
     *                              launch group call experience.
     */
    public void launch(final Context context,
                       final CallCompositeRemoteOptions remoteOptions,
                       final CallCompositeLocalOptions localOptions) {

        UUID groupId = null;
        String meetingLink = null;
        final CallType callType;

        final CallCompositeJoinMeetingLocator locator = remoteOptions.getLocator();
        if (locator instanceof CallCompositeGroupCallLocator) {
            callType = CallType.GROUP_CALL;
            groupId = ((CallCompositeGroupCallLocator) locator).getGroupId();
        } else {
            callType = CallType.TEAMS_MEETING;
            meetingLink = ((CallCompositeTeamsMeetingLinkLocator) locator).getMeetingLink();
        }

        configuration.setCallConfig(new CallConfiguration(
                remoteOptions.getCredential(),
                remoteOptions.getDisplayName(),
                groupId,
                meetingLink,
                callType));

        if (localOptions != null) {
            configuration.setCallCompositeLocalOptions(localOptions);
        }

        CallCompositeConfiguration.Companion.putConfig(instanceId, configuration);

        final Intent intent = new Intent(context, CallCompositeActivity.class);
        intent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId++);
        context.startActivity(intent);

    }

    /**
     * Set {@link CallCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeErrorCode} for values.</p>
     * <pre>
     *
     * &#47;&#47; set error handler
     * callComposite.setOnErrorHandler&#40;event -> {
     *     &#47;&#47; Process error event
     *     System.out.println&#40;event.getCause&#40;&#41;&#41;;
     *     System.out.println&#40;event.getErrorCode&#40;&#41;&#41;;
     * }&#41;;
     *
     *
     * &#47;&#47; remove error handler
     * callComposite.setOnErrorHandler&#40;null&#41;;
     *
     * </pre>
     *
     * @param errorHandler The {@link CallCompositeEventHandler}.
     */
    public void setOnErrorHandler(final CallCompositeEventHandler<CallCompositeErrorEvent> errorHandler) {
        configuration.getCallCompositeEventsHandler().setOnErrorHandler(errorHandler);
    }

    /**
     * Set {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; set remote participant joined handler
     * callComposite.setOnRemoteParticipantJoinedHandler&#40;event -> {
     *     &#47;&#47; Use call composite to set configurations for remote participant
     * }&#41;;
     *
     *
     * &#47;&#47; remove remote participant joined handler
     * callComposite.setOnRemoteParticipantJoinedHandler&#40;null&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void setOnRemoteParticipantJoinedHandler(
            final CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().setOnRemoteParticipantJoinedHandler(eventHandler);
    }

    /**
     * Set {@link CallCompositeParticipantViewData}.
     *
     * <p>
     *     Used to set Participant View Data (E.g. Avatar and displayName) to be used on this device only.
     * </p>
     * <p>
     *     This should be called from {@link #setOnRemoteParticipantJoinedHandler(CallCompositeEventHandler)}
     *     to assign Participant View Data when a Participant joins the meeting if you'd like to modify the
     *     Participants view data.
     * </p>
     *
     * @param identifier  The {@link CommunicationIdentifier}.
     * @param participantViewData The {@link CallCompositeParticipantViewData}.
     * @return {@link CallCompositeSetParticipantViewDataResult}.
     */
    public CallCompositeSetParticipantViewDataResult setRemoteParticipantViewData(
            final CommunicationIdentifier identifier, final CallCompositeParticipantViewData participantViewData) {
        return configuration.getRemoteParticipantsConfiguration()
                .setParticipantViewData(identifier, participantViewData);
    }
}
