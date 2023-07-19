// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallType;
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer;
import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo;
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingStateChangedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.PiPCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager;
import com.jakewharton.threetenabp.AndroidThreeTen;

import static com.azure.android.communication.ui.calling.CallCompositeExtentionsKt.createDebugInfoManager;
import static com.azure.android.communication.ui.calling.service.sdk.TypeConversionsKt.into;

import java.lang.ref.WeakReference;
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

    // on each launch, an InstanceID will be assigned and incremented.
    private static int instanceIdCounter = 0;
    private final int instanceId = instanceIdCounter++;

    private final CallCompositeConfiguration configuration;
    private WeakReference<DependencyInjectionContainer> diContainer;

    private WeakReference<CallCompositeActivity> activity;

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
     * final CallCompositeJoinLocator locator =
     *                 new CallCompositeGroupCallLocator&#40;UUID&#41;;
     * final CallCompositeJoinLocator locator =
     *                 new CallCompositeTeamsMeetingLinkLocator&#40;URL&#41;;
     * final CallCompositeRemoteOptions remoteOptions =
     *                 new CallCompositeRemoteOptions&#40;locator, credential, displayName&#41;;
     * callComposite.launch&#40;context, groupCallOptions&#41;;
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
     * final CallCompositeJoinLocator locator =
     *                 new CallCompositeGroupCallLocator&#40;UUID&#41;;
     * final CallCompositeJoinLocator locator =
     *                 new CallCompositeTeamsMeetingLinkLocator&#40;URL&#41;;
     * final CallCompositeRemoteOptions remoteOptions =
     *                 new CallCompositeRemoteOptions&#40;locator, credential, displayName&#41;;
     * callComposite.launch&#40;context, groupCallOptions&#41;;
     * final CallCompositeLocalOptions localOptions =
     *                 new CallCompositeLocalOptions&#40;participantViewData&#41;;
     * callComposite.launch&#40;context, groupCallOptions, localOptions&#41;;
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

        launchComposite(context, remoteOptions, localOptions, false);
    }

    /**
     * Add {@link CallCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeErrorCode} for values.</p>
     * <pre>
     *
     * &#47;&#47; add error handler
     * callComposite.addOnErrorEventHandler&#40;event -> {
     *     &#47;&#47; Process error event
     *     System.out.println&#40;event.getCause&#40;&#41;&#41;;
     *     System.out.println&#40;event.getErrorCode&#40;&#41;&#41;;
     * }&#41;;
     *
     * </pre>
     *
     * @param errorHandler The {@link CallCompositeEventHandler}.
     */
    public void addOnErrorEventHandler(final CallCompositeEventHandler<CallCompositeErrorEvent> errorHandler) {
        configuration.getCallCompositeEventsHandler().addOnErrorEventHandler(errorHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent} for values.</p>
     *
     * @param errorHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnErrorEventHandler(final CallCompositeEventHandler<CallCompositeErrorEvent> errorHandler) {
        configuration.getCallCompositeEventsHandler().removeOnErrorEventHandler(errorHandler);
    }

    /**
     * Add {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add remote participant joined handler
     * callComposite.addOnRemoteParticipantJoinedEventHandler&#40;event -> {
     *     &#47;&#47; Use call composite to set configurations for remote participant
     * }&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void addOnRemoteParticipantJoinedEventHandler(
            final CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnRemoteParticipantJoinedEventHandler(eventHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnRemoteParticipantJoinedEventHandler(
            final CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnRemoteParticipantJoinedEventHandler(eventHandler);
    }


    public void addOnMultitaskingStateChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeMultitaskingStateChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnMultitaskingStateChangedEventHandler(eventHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnMultitaskingStateChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeMultitaskingStateChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnMultitaskingStateChangedEventHandler(eventHandler);
    }

    /**
     * Set {@link CallCompositeParticipantViewData}.
     *
     * <p>
     *     Used to set Participant View Data (E.g. Avatar and displayName) to be used on this device only.
     * </p>
     * <p>
     *     This should be called from {@link #addOnRemoteParticipantJoinedEventHandler(CallCompositeEventHandler)}
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
                .setParticipantViewData(into(identifier), participantViewData);
    }

    /**
     * Get Call Composite Debug information.
     *
     * @return {@link CallCompositeDebugInfo}
     */
    public CallCompositeDebugInfo getDebugInfo(final Context context) {
        AndroidThreeTen.init(context.getApplicationContext());
        final DebugInfoManager debugInfoManager = getDebugInfoManager(context.getApplicationContext());
        return debugInfoManager.getDebugInfo();
    }

    /**
     * Display Call Composite if it was hidden by user going Back in navigation while on the call.
     * @param context
     */
    public void displayCallCompositeIfWasHidden(final Context context) {
        showUI(context, false);
    }

    /**
     * Hide call composite.
     */
    public void hide() {
        if (diContainer != null) {
            final CallCompositeActivity activity = this.activity.get();
            if (activity != null) {
                activity.hide();
            }
        }
    }

    void setDependencyInjectionContainer(final DependencyInjectionContainer diContainer) {
        this.diContainer = new WeakReference<>(diContainer);
    }

    void setActivity(final CallCompositeActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    private DebugInfoManager getDebugInfoManager(final Context context) {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer.get();
            if (container != null) {
                return container.getDebugInfoManager();
            }
        }
        return createDebugInfoManager(context.getApplicationContext());
    }

    private void launchComposite(final Context context,
                            final CallCompositeRemoteOptions remoteOptions,
                            final CallCompositeLocalOptions localOptions,
                            final boolean isTest) {
        AndroidThreeTen.init(context.getApplicationContext());

        UUID groupId = null;
        String meetingLink = null;
        final CallType callType;

        final CallCompositeJoinLocator locator = remoteOptions.getLocator();
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

        showUI(context, isTest);
    }

    private void showUI(final Context context,
                        final boolean isTest) {

        Class activityClass = CallCompositeActivity.class;

        if (configuration.getEnableMultitasking()) {
            activityClass = MultitaskingCallCompositeActivity.class;
        }
        if (configuration.getEnableSystemPiPWhenMultitasking()) {
            activityClass = PiPCallCompositeActivity.class;
        }

        CallCompositeInstanceManager.putCallComposite(instanceId, this);

        final Intent intent = new Intent(context, activityClass);
        intent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId);
        if (isTest) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    CallCompositeConfiguration getConfiguration() {
        return this.configuration;
    }

    void launchTest(final Context context,
                    final CallCompositeRemoteOptions remoteOptions,
                    final CallCompositeLocalOptions localOptions) {
        launchComposite(context, remoteOptions, localOptions, true);
    }
}
