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
import com.azure.android.communication.ui.calling.logger.DefaultLogger;
import com.azure.android.communication.ui.calling.logger.Logger;
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainerImpl;
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode;
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo;
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent;
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo;
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions;
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.PiPCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager;
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKCallAgentWrapper;
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKInstanceManager;
import com.azure.android.communication.ui.calling.service.sdk.IncomingCallWrapper;
import com.azure.android.communication.ui.calling.redux.action.PipAction;
import com.azure.android.communication.ui.calling.utilities.TestHelper;
import com.jakewharton.threetenabp.AndroidThreeTen;

import static com.azure.android.communication.ui.calling.CallCompositeExtentionsKt.createDebugInfoManager;
import static com.azure.android.communication.ui.calling.service.sdk.TypeConversionsKt.into;

import java.util.List;
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

    static DependencyInjectionContainer diContainer;

    // on each launch, an InstanceID will be assigned and incremented.
    private static int instanceIdCounter = 0;
    private final int instanceId = instanceIdCounter++;
    private final CallCompositeConfiguration configuration;
    private CallingSDKCallAgentWrapper callAgentWrapper;
    private IncomingCallWrapper incomingCallWrapper;
    private Logger logger = new DefaultLogger();

    CallComposite(final CallCompositeConfiguration configuration) {
        this.configuration = configuration;
        diContainer = null;
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
     * @param context       The android context used to start the Composite.
     * @param remoteOptions The {@link CallCompositeRemoteOptions} has remote parameters to
     *                      launch call experience.
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
     * @param context       The android context used to start the Composite.
     * @param remoteOptions The {@link CallCompositeRemoteOptions} has remote parameters to
     *                      launch group call experience.
     * @param localOptions  The {@link CallCompositeLocalOptions} has local parameters to
     *                      launch group call experience.
     */
    public void launch(final Context context,
                       final CallCompositeRemoteOptions remoteOptions,
                       final CallCompositeLocalOptions localOptions) {

        launchComposite(context, remoteOptions, localOptions, false);
    }

    /**
     * Handle incoming call push notification.
     *
     * <pre>
     *
     * final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
     *                 new CommunicationTokenRefreshOptions&#40;tokenRefresher, true&#41;;
     * final CommunicationTokenCredential credential =
     *                 new CommunicationTokenCredential&#40;communicationTokenRefreshOptions&#41;;
     * final CallCompositePushNotificationInfo pushNotificationInfo =
     *                 new CallCompositePushNotificationInfo&#40;notificationMap&#41;;
     * final CallCompositeRemoteOptions remoteOptions =
     *                 new CallCompositeRemoteOptions&#40;pushNotificationInfo, credential, displayName&#41;;
     * callComposite.launch&#40;context, remoteOptions&#41;;
     *
     * </pre>
     *
     * @param context           The android context used to start the Composite.
     * @param remoteOptions     The {@link CallCompositeRemoteOptions} has remote parameters to
     *                              launch group call experience.
     */
    public void handlePushNotification(final Context context,
                       final CallCompositeRemoteOptions remoteOptions) {

        handlePushNotification(context, remoteOptions, false);
    }

    /**
     * Add {@link CallCompositeEventHandler}.
     *
     * <p> Add a callback for Call Composite dismissed Event.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeErrorCode} for values.</p>
     * <pre>
     *
     * &#47;&#47; add on dismissed event handler.
     * callComposite.addOnDismissedEventHandler&#40;event -> {
     * }&#41;;
     *
     * </pre>
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void addOnDismissedEventHandler(final CallCompositeEventHandler<CallCompositeDismissedEvent> handler) {
        configuration.getCallCompositeEventsHandler().addOnDismissedEventHandler(handler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * <p> Remove a callback for Call Composite dismissed Event.
     * See {@link CallCompositeDismissedEvent} for values.</p>
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void removeOnDismissedEventHandler(final CallCompositeEventHandler<CallCompositeDismissedEvent> handler) {
        configuration.getCallCompositeEventsHandler().removeOnExitEventHandler(handler);
    }

    /**
     * Dismiss composite. If call is in progress, user will leave a call.
     *
     */
    public void dismiss() {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            container.getCompositeExitManager().exit();
        }
    }

    /**
     * Dismiss composite. Cleanup memory hold by call agent.
     *
     */
    public void dispose() {
        dismiss();
        if (callAgentWrapper != null) {
            callAgentWrapper.dispose();
        }
        CallingSDKInstanceManager.INSTANCE.setCallingSDKCallAgentWrapper(null);
    }

    /**
     * Accept incoming call.
     *
     */
    public void acceptIncomingCall(final Context context,
                                   final CallCompositeLocalOptions localOptions) {
        logger.info("Call Composite acceptIncomingCall");
        AndroidThreeTen.init(context.getApplicationContext());

        configuration.setCallConfig(new CallConfiguration(
                configuration.getCallConfig().getCommunicationTokenCredential(),
                configuration.getCallConfig().getDisplayName(),
                null,
                null,
                null,
                null,
                CallType.ONE_TO_N_CALL_INCOMING,
                null,
                null));

        if (localOptions != null) {
            configuration.setCallCompositeLocalOptions(localOptions);
        }

        diContainer = new DependencyInjectionContainerImpl(
                instanceId,
                context.getApplicationContext(),
                this,
                TestHelper.INSTANCE.getCallingSDK(),
                TestHelper.INSTANCE.getVideoStreamRendererFactory(),
                TestHelper.INSTANCE.getCoroutineContextProvider()
        );

        showUI(context, false, true);
    }

    /**
     * Decline incoming call.
     *
     */
    public void declineIncomingCall() {
        if (incomingCallWrapper != null) {
            incomingCallWrapper.declineCall();
        }
    }

    /**
     * Add {@link CallCompositeEventHandler}.
     *
     * <p> Add a callback for Call Composite Error Events.
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
     * <p> Remove a callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent} for values.</p>
     *
     * @param errorHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnErrorEventHandler(final CallCompositeEventHandler<CallCompositeErrorEvent> errorHandler) {
        configuration.getCallCompositeEventsHandler().removeOnErrorEventHandler(errorHandler);
    }

    /**
     * Add on call state changed event handler {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add call state changed handler
     * callComposite.addOnCallStateChangedEventHandler&#40;event -> {
     *     &#47;&#47; Use call composite call state
     * }&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void addOnCallStateChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeCallStateChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnCallStateChangedEventHandler(eventHandler);
    }

    /**
     * Remove on call state changed event handler {@link CallCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnCallStateChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeCallStateChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnCallStateEventHandler(eventHandler);
    }

    /**
     * Add on incoming call event handler {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add on incoming call handler
     * callComposite.addOnIncomingCallEventHandler&#40;event -> {
     *     &#47;&#47; Use call composite incoming call to accept and dcline
     * }&#41;;
     *
     * </pre>
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void addOnIncomingCallEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallEvent> handler) {
        configuration.getCallCompositeEventsHandler().addOnIncomingCallEventHandler(handler);
    }

    /**
     * Remove on incoming call event handler {@link CallCompositeEventHandler}.
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void removeOnIncomingCallEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallEvent> handler) {
        configuration.getCallCompositeEventsHandler().removeOnIncomingCallEventHandler(handler);
    }

    /**
     * Add on incoming call end event handler {@link CallCompositeIncomingCallEndEvent}.
     * @param handler The {@link CallCompositeIncomingCallEndEvent}.
     */
    public void addOnIncomingCallEndEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> handler) {
        configuration.getCallCompositeEventsHandler().addOnIncomingCallEndEventHandler(handler);
    }

    /**
     * Start audio session
     *
     */
    public void startAudio() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                container.getCallingService().startAudio();
            }
        }
    }

    /**
     * Stop audio session.
     *
     */
    public void stopAudio() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                container.getCallingService().stopAudio();
            }
        }
    }

    /**
     * Turn on video.
     *
     */
    public void turnMicOn() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                container.getCallingService().turnMicOn();
            }
        }
    }

    /**
     * Turn off video.
     *
     */
    public void turnMicOff() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                container.getCallingService().turnMicOff();
            }
        }
    }

    /**
     * Turn on video.
     *
     */
    public void hold() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                container.getCallingService().hold();
            }
        }
    }

    /**
     * Turn off video.
     *
     */
    public void resume() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                container.getCallingService().resume();
            }
        }
    }

    /**
     * Remove on incoming call event handler {@link CallCompositeIncomingCallEvent}.
     *
     * @param handler The {@link CallCompositeIncomingCallEndEvent}.
     */
    public void removeOnIncomingCallEndEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallEndEvent> handler) {
        configuration.getCallCompositeEventsHandler().removeOnIncomingCallEndEventHandler(handler);
    }

    /**
     * Get Call State.
     *
     * @return {@link CallCompositeCallStateCode}
     */
    public CallCompositeCallStateCode getCallState() {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            return container.getCallStateHandler().getCallCompositeCallState();
        }

        return CallCompositeCallStateCode.NONE;
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


    public void addOnPictureInPictureChangedEventHandler(
            final CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnMultitaskingStateChangedEventHandler(eventHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnPictureInPictureChangedEventHandler(
            final CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnMultitaskingStateChangedEventHandler(eventHandler);
    }

    /**
     * Set {@link CallCompositeParticipantViewData}.
     *
     * <p>
     * Used to set Participant View Data (E.g. Avatar and displayName) to be used on this device only.
     * </p>
     * <p>
     * This should be called from {@link #addOnRemoteParticipantJoinedEventHandler(CallCompositeEventHandler)}
     * to assign Participant View Data when a Participant joins the meeting if you'd like to modify the
     * Participants view data.
     * </p>
     *
     * @param identifier          The {@link CommunicationIdentifier}.
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
     *
     * @param context
     */
    public void displayCallCompositeIfWasHidden(final Context context) {
        showUI(context, false, false);
    }

    /**
     * RegisterPushNotification to receive incoming call notification.
     *
     * @param context The {@link Context}.
     * @param options The {@link CallCompositePushNotificationOptions} if call is already in progress
     *                existing display name and CommunicationTokenCredential is used.
     */
    public void registerPushNotification(final Context context, final CallCompositePushNotificationOptions options) {
        initializeCallAgent();
        // for device token, we need to set the call config. with ONE_TO_N_CALL_INCOMING
        configuration.setCallConfig(new CallConfiguration(
                options.getTokenCredential(),
                options.getDisplayName(),
                null,
                null,
                null,
                null,
                CallType.ONE_TO_N_CALL_INCOMING,
                null,
                null));
        callAgentWrapper.registerPushNotification(context,
                options.getDisplayName(),
                options.getTokenCredential(),
                options.getDeviceRegistrationToken());
    }

    /**
     * Hide call composite.
     */
    public void hide() {
        if (diContainer != null) {
            diContainer.getAppStore().dispatch(new PipAction.HideRequested());
        }
    }

    private DebugInfoManager getDebugInfoManager(final Context context) {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            return container.getDebugInfoManager();
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
        String roomId = null;
        CallCompositeParticipantRole roomRole = null;
        final CallType callType;
        List<String> participants = null;

        final CallCompositeJoinLocator locator = remoteOptions.getLocator();
        if (locator != null) {
            if (locator instanceof CallCompositeGroupCallLocator) {
                callType = CallType.GROUP_CALL;
                groupId = ((CallCompositeGroupCallLocator) locator).getGroupId();
            } else if (locator instanceof CallCompositeRoomLocator) {
                callType = CallType.ROOMS_CALL;
                final CallCompositeRoomLocator roomLocator = (CallCompositeRoomLocator) locator;
                roomId = roomLocator.getRoomId();
            } else {
                callType = CallType.TEAMS_MEETING;
                meetingLink = ((CallCompositeTeamsMeetingLinkLocator) locator).getMeetingLink();
            }
        } else {
            callType = CallType.ONE_TO_N_CALL_OUTGOING;
            participants = remoteOptions.getStartCallOptions().getParticipants();
        }

        if (localOptions != null) {
            configuration.setCallCompositeLocalOptions(localOptions);
            roomRole = localOptions.getRoleHint();
        }

        configuration.setCallConfig(new CallConfiguration(
                remoteOptions.getCredential(),
                remoteOptions.getDisplayName(),
                groupId,
                meetingLink,
                roomId,
                roomRole,
                callType,
                participants,
                null
                ));


        diContainer = new DependencyInjectionContainerImpl(
                instanceId,
                context.getApplicationContext(),
                this,
                TestHelper.INSTANCE.getCallingSDK(),
                TestHelper.INSTANCE.getVideoStreamRendererFactory(),
                TestHelper.INSTANCE.getCoroutineContextProvider()
        );

        showUI(context, isTest, false);
    }

    private void showUI(final Context context,
                        final boolean isTest,
                        final boolean isAcceptIncomingCall) {

        Class activityClass = CallCompositeActivity.class;

        if (configuration.getEnableMultitasking()) {
            activityClass = MultitaskingCallCompositeActivity.class;
        }
        if (configuration.getEnableSystemPiPWhenMultitasking()) {
            activityClass = PiPCallCompositeActivity.class;
        }

        CallCompositeInstanceManager.putCallComposite(instanceId, this);

        initializeCallAgent();

        final Intent intent = new Intent(context, activityClass);
        intent.putExtra(CallCompositeActivity.KEY_INSTANCE_ID, instanceId);
        if (isTest || isAcceptIncomingCall) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    private void handlePushNotification(final Context context,
                                 final CallCompositeRemoteOptions remoteOptions,
                                 final boolean isTest) {
        AndroidThreeTen.init(context.getApplicationContext());

        final CallType callType = CallType.ONE_TO_N_CALL_INCOMING;

        final CallCompositePushNotificationInfo pushNotificationInfo = remoteOptions.getPushNotificationInfo();

        if (pushNotificationInfo == null) {
            throw new IllegalArgumentException("PushNotificationInfo cannot be null");
        }

        configuration.setCallConfig(new CallConfiguration(
                remoteOptions.getCredential(),
                remoteOptions.getDisplayName(),
                null,
                null,
                null,
                null,
                callType,
                null,
                pushNotificationInfo));

        if (configuration.getCallCompositeEventsHandler().getOnIncomingCallEventHandlers() == null) {
            throw new IllegalArgumentException("IncomingCallEventHandler cannot be null");
        }

        initializeCallAgent();

        incomingCallWrapper.handlePushNotification(context.getApplicationContext(),
                remoteOptions.getDisplayName(),
                remoteOptions.getCredential(),
                pushNotificationInfo.getNotificationInfo());
    }

    private void initializeCallAgent() {
        callAgentWrapper = CallingSDKInstanceManager.INSTANCE.getCallingSDKCallAgentWrapper();
        if (callAgentWrapper == null) {
            callAgentWrapper = new CallingSDKCallAgentWrapper(logger);
            CallingSDKInstanceManager.INSTANCE.
                    setCallingSDKCallAgentWrapper(callAgentWrapper);
        }
        // as for every new composite handlers are different, we need to set them again.
        if (incomingCallWrapper == null) {
            incomingCallWrapper = new IncomingCallWrapper(logger,
                    CallingSDKInstanceManager.INSTANCE.getCallingSDKCallAgentWrapper(),
                    configuration.getCallCompositeEventsHandler().getOnIncomingCallEventHandlers(),
                    configuration.getCallCompositeEventsHandler().getOnIncomingCallEndEventHandlers());
            callAgentWrapper.setIncomingCallWrapper(incomingCallWrapper);
        }
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
