// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import static com.azure.android.communication.ui.calling.CallCompositeExtentionsKt.createDebugInfoManager;
import static com.azure.android.communication.ui.calling.service.sdk.TypeConversionsKt.into;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallType;
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer;
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainerImpl;
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode;
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo;
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallCancelledEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions;
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent;
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.PiPCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager;
import com.azure.android.communication.ui.calling.redux.action.PipAction;
import com.azure.android.communication.ui.calling.utilities.TestHelper;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Collection;
import java.util.Collections;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    CallComposite(final CallCompositeConfiguration configuration) {
        this.configuration = configuration;
        diContainer = null;
    }

    /**
     * Launch group call composite.
     * @deprecated Use {@link #launch(Context, CallCompositeJoinLocator)} instead.
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
    @Deprecated
    public void launch(final Context context, final CallCompositeRemoteOptions remoteOptions) {
        launch(context, remoteOptions, null);
    }

    /**
     * Launch composite to join a groupCall/TeamsMeeting.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}}.
     * @param activityContext   The android context used to start the Composite.
     * @param locator The {@link CallCompositeJoinLocator}.
     */
    public void launch(final Context activityContext, final CallCompositeJoinLocator locator) {
    }

    /**
     * Launch composite to join a groupCall/TeamsMeeting with {@link CallCompositeLocalOptions}.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}}.
     * @param activityContext   The android context used to start the Composite.
     * @param locator The {@link CallCompositeJoinLocator}.
     * @param localOptions  The {@link CallCompositeLocalOptions} has local parameters to
     *                      launch call experience.
     */
    public void launch(final Context activityContext,
                       final CallCompositeJoinLocator locator,
                       final CallCompositeLocalOptions localOptions) {

    }

    /**
     * Launch composite to dial participants.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}}.
     * @param activityContext   The android context used to start the Composite.
     * @param participants  The collection of {@link CommunicationIdentifier} to start the call.
     * @param localOptions  The {@link CallCompositeLocalOptions} has local parameters to
     *                      launch call experience.
     */
    public void launch(final Context activityContext,
                       final Collection<CommunicationIdentifier> participants,
                       final CallCompositeLocalOptions localOptions) {

    }

    /**
     * Launch composite to dial participants.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     * @param activityContext   The android context used to start the Composite.
     * @param participants  The collection of {@link CommunicationIdentifier} to start the call.
     */
    public void launch(final Context activityContext,
                       final Collection<CommunicationIdentifier> participants) {

    }

    /**
     * Launch group call composite.
     * @deprecated Use {@link #launch(Context, CallCompositeJoinLocator, CallCompositeLocalOptions)} instead.
     *
     * @param context       The android context used to start the Composite.
     * @param remoteOptions The {@link CallCompositeRemoteOptions} has remote parameters to
     *                      launch group call experience.
     * @param localOptions  The {@link CallCompositeLocalOptions} has local parameters to
     *                      launch group call experience.
     */
    @Deprecated
    public void launch(final Context context,
                       final CallCompositeRemoteOptions remoteOptions,
                       final CallCompositeLocalOptions localOptions) {

        launchComposite(context, remoteOptions, localOptions, false);
    }

    /**
     * Handle incoming call push notification.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     * @param info     The {@link CallCompositePushNotificationInfo}.
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void> handlePushNotification(final CallCompositePushNotificationInfo info) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Add {@link CallCompositeEventHandler}.
     *
     * <p> Add a callback for Call Composite Audio Selection Changed Event.
     * See {@link CallCompositeAudioSelectionChangedEvent} for values.</p>
     * <pre>
     *
     * &#47;&#47; add on audio selection changed event handler.
     * callComposite.addOnAudioSelectionChangedEventHandler&#40;event -> {
     *     &#47;&#47; Process audio selection changed event
     *     System.out.println&#40;event.getSelectionType&#40;&#41;&#41;;
     * }&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void addOnAudioSelectionChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeAudioSelectionChangedEvent> eventHandler) {
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * <p> Remove a callback for Call Composite Audio Selection Changed Event.
     * See {@link CallCompositeAudioSelectionChangedEvent} for values.</p>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnAudioSelectionChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeAudioSelectionChangedEvent> eventHandler) {
    }


    /**
     * Accept incoming call.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     activityContext
     * @param activityContext   The android context used to start the Composite.
     * @param callId The call id.
     * @param localOptions The {@link CallCompositeLocalOptions}.
     */
    public void acceptIncomingCall(final Context activityContext,
                                   final String callId,
                                   final CallCompositeLocalOptions localOptions) {
    }

    /**
     * Accept incoming call.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     * @param activityContext   The android context used to start the Composite.
     * @param callId The call id.
     */
    public void acceptIncomingCall(final Context activityContext,
                                   final String callId) {
    }

    /**
     * Reject incoming call.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     * @param callId The call id.
     */
    public CompletableFuture<Void> rejectIncomingCall(final String callId)  {
        return null;
    }


    /**
     * Add on incoming call event handler {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add on incoming call event handler.
     * callComposite.addOnIncomingCallEventHandler&#40;event -> {
     *     &#47;&#47; Process incoming call event
     *     System.out.println&#40;event.getCallId&#40;&#41;&#41;;
     *     System.out.println&#40;event.getCallerDisplayName&#40;&#41;&#41;;
     * }&#41;;
     *
     * </pre>
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void addOnIncomingCallEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallEvent> handler) {
    }

    /**
     * Remove on incoming call event handler {@link CallCompositeEventHandler}.
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void removeOnIncomingCallEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallEvent> handler) {
    }

    /**
     * Add on incoming call cancelled event handler {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add on incoming call cancelled event handler.
     * callComposite.addOnIncomingCallCancelledEventHandler&#40;event -> {
     *     &#47;&#47; Process incoming call cancelled event
     *     System.out.println&#40;event.getCallId&#40;&#41;&#41;;
     * }&#41;;
     *
     * </pre>
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void addOnIncomingCallCancelledEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallCancelledEvent> handler) {
    }

    /**
     * Remove on incoming call cancelled event handler {@link CallCompositeEventHandler}.
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void removeOnIncomingCallCancelledEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallCancelledEvent> handler) {
    }

    /**
     * Mute incoming audio.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  muteIncomingAudio() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
        }
        return null;
    }

    /**
     * Un mute incoming audio.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  unmuteIncomingAudio() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
        }
        return null;
    }

    /**
     * Mute outgoing audio.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  muteOutgoingAudio() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
        }
        return null;
    }

    /**
     * Un mute outgoing audio.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  unmuteOutgoingAudio() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
        }
        return null;
    }

    /**
     * Hold call.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  hold() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
        }
        return null;
    }

    /**
     * Resume call.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  resume() {
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
        }
        return null;
    }

    /**
     * RegisterPushNotification to receive incoming call notification.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     * @param deviceRegistrationToken The device registration token.
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void> registerPushNotification(final String deviceRegistrationToken) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * UnregisterPushNotification to stop receiving incoming call notification.
     * Build composite using {@link CallCompositeBuilder#build(Context, CommunicationTokenCredential)}.
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void> unregisterPushNotification() {
        return CompletableFuture.completedFuture(null);
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
     * Add {@link CallCompositeEventHandler}.
     *
     * <p> Add a callback for Call Composite User Reported Issue Event.
     * See {@link CallCompositeUserReportedIssueEvent} for values.</p>
     * <pre>
     * &#47;&#47; add on user reported event handler.
     * callComposite.addOnUserReportedEventHandler&#40;event -> {
     *     &#47;&#47; Process user reported event
     *     System.out.println&#40;event.getUserMessage()&#40;&#41;&#41;;
     *     System.out.println&#40;event.getScreenshot()&#40;&#41;&#41;;
     *     DebugInfo info = event.getDebugInfo();
     * }&#41;;
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void addOnUserReportedEventHandler(
            final CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnUserReportedEventHandler(eventHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     * <p> Remove a callback for Call Composite user reported Event.
     * See {@link CallCompositeUserReportedIssueEvent} for values.</p>
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void removeOnUserReportedEventHandler(
            final CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> handler) {
        configuration.getCallCompositeEventsHandler().removeOnUserReportedEventHandler(handler);
    }


    /**
     * Dismiss composite. If call is in progress, user will leave a call.
     */
    public void dismiss() {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            container.getCompositeExitManager().exit();
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

    /**
     * Add {@link CallCompositeEventHandler}
     *
     * @param eventHandler
     */
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
     * Brings Call Composite to foreground if it was send to background by {@link #sendToBackground()}
     * ot by user going back in navigation while on the call.
     *
     * @param context
     */
    public void bringToForeground(final Context context) {
        showUI(context, false);
    }

    /**
     * Hide call composite. If {@link CallCompositeMultitaskingOptions} is constructed with
     * enableSystemPictureInPictureWhenMultitasking set to true, then Pip will be displayed.
     */
    public void sendToBackground() {
        if (diContainer != null) {
            diContainer.getAppStore().dispatch(new PipAction.HideRequested());
        }
    }

    private DebugInfoManager getDebugInfoManager(final Context context) {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            return container.getDebugInfoManager();
        }
        return createDebugInfoManager(context.getApplicationContext(), Collections::emptyList);
    }

    /* <TEST_FEATURE>
    public String testString() {
        return "testString";
    }
    </TEST_FEATURE> */

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

        diContainer = new DependencyInjectionContainerImpl(
                instanceId,
                context.getApplicationContext(),
                this,
                TestHelper.INSTANCE.getCallingSDK(),
                TestHelper.INSTANCE.getVideoStreamRendererFactory(),
                TestHelper.INSTANCE.getCoroutineContextProvider()
        );

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
