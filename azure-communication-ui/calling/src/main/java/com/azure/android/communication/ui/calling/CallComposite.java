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
import com.azure.android.communication.ui.calling.logger.DefaultLogger;
import com.azure.android.communication.ui.calling.logger.Logger;
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
import com.azure.android.communication.ui.calling.models.CallCompositePushNotification;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantLeftEvent;
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingIdLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent;
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.PiPCallCompositeActivity;
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager;
import com.azure.android.communication.ui.calling.redux.action.PipAction;
/* <RTT_POC>
import com.azure.android.communication.ui.calling.redux.action.RttAction;
</RTT_POC> */
import com.azure.android.communication.ui.calling.redux.state.CallingStatus;
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKInitializer;
import com.azure.android.communication.ui.calling.utilities.TestHelper;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Collection;
import java.util.Collections;

/*  <CALL_START_TIME> */
import java.util.Date;
/* </CALL_START_TIME> */
import java.util.List;
/* <RTT_POC>
import java.util.Timer;
import java.util.TimerTask;
<RTT_POC> */

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

    // helps to dispose the previous instance of CallingSDKInitialization
    private static CallingSDKInitializer sdkInitializer;

    // on each launch, an InstanceID will be assigned and incremented.
    private static int instanceIdCounter = 0;
    private final int instanceId = instanceIdCounter++;
    private final CallCompositeConfiguration configuration;
    private final Logger logger = new DefaultLogger();

    CallComposite(final CallCompositeConfiguration configuration) {
        this.configuration = configuration;
        diContainer = null;
        disposeSDKInitializer();
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
     * Launch group call composite.
     * @deprecated Use {@link #launch(Context, CallCompositeJoinLocator, CallCompositeLocalOptions)} instead.
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
    @Deprecated
    public void launch(final Context context,
                       final CallCompositeRemoteOptions remoteOptions,
                       final CallCompositeLocalOptions localOptions) {

        launchComposite(context, remoteOptions, localOptions, false);
    }

    /**
     * Launch composite to join a groupCall/TeamsMeeting.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param activityContext   The android context used to start the Composite.
     * @param locator The {@link CallCompositeJoinLocator}.
     */
    public void launch(final Context activityContext, final CallCompositeJoinLocator locator) {
        launch(activityContext, locator, null);
    }

    /**
     * Launch composite to join a groupCall/TeamsMeeting with {@link CallCompositeLocalOptions}.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param activityContext   The android context used to start the Composite.
     * @param locator The {@link CallCompositeJoinLocator}.
     * @param localOptions  The {@link CallCompositeLocalOptions} has local parameters to
     *                      launch call experience.
     */
    public void launch(final Context activityContext,
                       final CallCompositeJoinLocator locator,
                       final CallCompositeLocalOptions localOptions) {
        launchComposite(activityContext, locator, null, null, localOptions, false);
    }

    /**
     * Launch composite to dial participants.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param activityContext   The android context used to start the Composite.
     * @param participants  The collection of {@link CommunicationIdentifier} to start the call.
     * @param localOptions  The {@link CallCompositeLocalOptions} has local parameters to
     *                      launch call experience.
     */
    public void launch(final Context activityContext,
                       final Collection<CommunicationIdentifier> participants,
                       final CallCompositeLocalOptions localOptions) {
        launchComposite(activityContext, null, participants, null, localOptions, false);
    }

    /**
     * Launch composite to dial participants.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param activityContext   The android context used to start the Composite.
     * @param participants  The collection of {@link CommunicationIdentifier} to start the call.
     */
    public void launch(final Context activityContext,
                       final Collection<CommunicationIdentifier> participants) {
        launchComposite(activityContext, null, participants, null, null, false);
    }


    /**
     * Accept incoming call.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param activityContext   The android context used to start the Composite.
     * @param incomingCallId The call id.
     * @param localOptions The {@link CallCompositeLocalOptions}.
     */
    public void accept(final Context activityContext,
                       final String incomingCallId,
                       final CallCompositeLocalOptions localOptions) {
        launchComposite(activityContext, null, null, incomingCallId, localOptions, false);
    }

    /**
     * Accept incoming call.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param activityContext   The android context used to start the Composite.
     * @param incomingCallId The call id.
     */
    public void accept(final Context activityContext,
                       final String incomingCallId) {
        launchComposite(activityContext, null, null, incomingCallId, null, false);
    }

    /**
     * Reject incoming call.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param incomingCallId The call id.
     */
    public CompletableFuture<Void> reject(final String incomingCallId) {
        return initializeCallingSDK().rejectIncomingCall(incomingCallId);
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
        configuration.getCallCompositeEventsHandler().addOnIncomingCallCancelledEventHandler(handler);
    }

    /**
     * Remove on incoming call cancelled event handler {@link CallCompositeEventHandler}.
     *
     * @param handler The {@link CallCompositeEventHandler}.
     */
    public void removeOnIncomingCallCancelledEventHandler(
            final CallCompositeEventHandler<CallCompositeIncomingCallCancelledEvent> handler) {
        configuration.getCallCompositeEventsHandler().removeOnIncomingCallCancelledEventHandler(handler);
    }

    /**
     * Hold call.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  hold() {
        final CompletableFuture<Void> holdFuture = new CompletableFuture<>();
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            container.getCallingService().hold().whenComplete((aVoid, throwable) -> {
                if (throwable != null) {
                    holdFuture.completeExceptionally(throwable);
                } else {
                    holdFuture.complete(aVoid);
                }
            });
        } else {
            holdFuture.completeExceptionally(new IllegalStateException("CallComposite is not initialized"));
        }
        return holdFuture;
    }

    /**
     * Resume call.
     *
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void>  resume() {
        final CompletableFuture<Void> resumeFuture = new CompletableFuture<>();
        if (diContainer != null) {
            final DependencyInjectionContainer container = diContainer;
            container.getCallingService().resume().whenComplete((aVoid, throwable) -> {
                if (throwable != null) {
                    resumeFuture.completeExceptionally(throwable);
                } else {
                    resumeFuture.complete(aVoid);
                }
            });
        } else {
            resumeFuture.completeExceptionally(new IllegalStateException("CallComposite is not initialized"));
        }
        return resumeFuture;
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
            container.getCompositeExitManager().exit(() -> {
                disposeSDKInitializer();
                return null;
            });
        } else {
            disposeSDKInitializer();
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
     * <p> Add a callback for Call Composite Audio Selection Changed Event.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent}
     * for values.</p>
     * <pre>
     *
     * &#47;&#47; add audio selection changed handler
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
        configuration.getCallCompositeEventsHandler().addOnAudioSelectionChangedEventHandler(eventHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * <p> Remove a callback for Call Composite Audio Selection Changed Event.
     * See {@link com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent}
     * for values.</p>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnAudioSelectionChangedEventHandler(
            final CallCompositeEventHandler<CallCompositeAudioSelectionChangedEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnAudioSelectionChangedEventHandler(eventHandler);
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
     * Add {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add remote participant left handler
     * callComposite.addOnRemoteParticipantLeftEventHandler&#40;event -> {
     *     &#47;&#47; Use call composite to set configurations for remote participant
     * }&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void addOnRemoteParticipantLeftEventHandler(
            final CallCompositeEventHandler<CallCompositeRemoteParticipantLeftEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnRemoteParticipantLeftEventHandler(eventHandler);
    }

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    public void removeOnRemoteParticipantLeftEventHandler(
            final CallCompositeEventHandler<CallCompositeRemoteParticipantLeftEvent> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnRemoteParticipantLeftEventHandler(eventHandler);
    }

    /**
     * Add {@link CallCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add on call start time updated handler
     * callComposite.addOnCallStartTimeUpdatedEventHandler&#40;event -> {
     *     &#47;&#47; Use call composite to set configurations for call start time
     * }&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    /* <CALL_START_TIME> */
    public void addOnCallStartTimeUpdatedEventHandler(
            final CallCompositeEventHandler<Date> eventHandler) {
        configuration.getCallCompositeEventsHandler().addOnCallStartTimeUpdatedEventHandler(eventHandler);
    }
    /* </CALL_START_TIME> */

    /**
     * Remove {@link CallCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallCompositeEventHandler}.
     */
    /* <CALL_START_TIME> */
    public void removeOnCallStartTimeUpdatedEventHandler(
            final CallCompositeEventHandler<Date> eventHandler) {
        configuration.getCallCompositeEventsHandler().removeOnCallStartTimeUpdatedEventHandler(eventHandler);
    }
    /* </CALL_START_TIME> */

    /**
     * Get Call Start Time.
     *
     * @return {@link Date}
     */
    /* <CALL_START_TIME> */
    public Date getCallStartTime() {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            return container.getCallingService().getCallStartTime();
        }
        return null;
    }
    /* </CALL_START_TIME> */

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
        final boolean hasCallComposite = CallCompositeInstanceManager.hasCallComposite(instanceId);
        if (hasCallComposite) {
            final DependencyInjectionContainer container = diContainer;
            if (container != null) {
                final CallingStatus currentStatus =
                        container.getAppStore().getCurrentState().getCallState().getCallingStatus();
                if (currentStatus != CallingStatus.DISCONNECTING
                        && currentStatus != CallingStatus.DISCONNECTED) {
                    showUI(context, false);
                }
            }
        }
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

    /**
     * Handle incoming call push notification.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param pushNotification     The {@link CallCompositePushNotification}.
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void> handlePushNotification(final CallCompositePushNotification pushNotification) {
        return initializeCallingSDK().handlePushNotification(pushNotification);
    }

    /**
     * RegisterPushNotification to receive incoming call notification.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @param deviceRegistrationToken The device registration token.
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void> registerPushNotification(final String deviceRegistrationToken) {
        return initializeCallingSDK().registerPushNotification(deviceRegistrationToken);
    }

    /**
     * UnregisterPushNotification to stop receiving incoming call notification.
     * Build {@link CallCompositeBuilder} using {@link CommunicationTokenCredential} and application {@link Context} .
     * @return {@link CompletableFuture} of {@link Void}.
     */
    public CompletableFuture<Void> unregisterPushNotification() {
        return initializeCallingSDK().unregisterPushNotification();
    }

    private DebugInfoManager getDebugInfoManager(final Context context) {
        final DependencyInjectionContainer container = diContainer;
        if (container != null) {
            return container.getDebugInfoManager();
        }
        return createDebugInfoManager(context.getApplicationContext(), Collections::emptyList);
    }


    private void launchComposite(final Context context,
                                 final CallCompositeRemoteOptions remoteOptions,
                                 final CallCompositeLocalOptions localOptions,
                                 final boolean isTest) {
        AndroidThreeTen.init(context.getApplicationContext());

        UUID groupId = null;
        String meetingLink = null;
        String meetingId = null;
        String meetingPasscode = null;
        String roomId = null;
        final CallType callType;

        final CallCompositeJoinLocator locator = remoteOptions.getLocator();
        if (locator instanceof CallCompositeGroupCallLocator) {
            callType = CallType.GROUP_CALL;
            groupId = ((CallCompositeGroupCallLocator) locator).getGroupId();
        }  else if (locator instanceof CallCompositeTeamsMeetingIdLocator) {
            callType = CallType.TEAMS_MEETING;
            final CallCompositeTeamsMeetingIdLocator teamsMeetingIdLocator =
                    (CallCompositeTeamsMeetingIdLocator) locator;
            meetingId = teamsMeetingIdLocator.getMeetingId();
            meetingPasscode = teamsMeetingIdLocator.getMeetingPasscode();
        } else if (locator instanceof CallCompositeTeamsMeetingLinkLocator) {
            callType = CallType.TEAMS_MEETING;
            meetingLink = ((CallCompositeTeamsMeetingLinkLocator) locator).getMeetingLink();
        } else if (locator instanceof CallCompositeRoomLocator) {
            callType = CallType.ROOMS_CALL;
            final CallCompositeRoomLocator roomLocator = (CallCompositeRoomLocator) locator;
            roomId = roomLocator.getRoomId();
        } else {
            throw new CallCompositeException("Not supported Call Locator type");
        }

        launchComposite(context,
                remoteOptions,
                localOptions,
                isTest,
                groupId,
                meetingLink,
                meetingId,
                meetingPasscode,
                roomId,
                callType,
                null,
                null
        );
    }

    private void launchComposite(final Context context,
                                 final CallCompositeJoinLocator locator,
                                 final Collection<CommunicationIdentifier> participants,
                                 final String incomingCallId,
                                 final CallCompositeLocalOptions localOptions,
                                 final boolean isTest) {
        AndroidThreeTen.init(context.getApplicationContext());

        UUID groupId = null;
        String meetingLink = null;
        String meetingId = null;
        String meetingPasscode = null;
        String roomId = null;
        final CallType callType;

        if (locator instanceof CallCompositeGroupCallLocator) {
            callType = CallType.GROUP_CALL;
            groupId = ((CallCompositeGroupCallLocator) locator).getGroupId();
        } else if (locator instanceof CallCompositeTeamsMeetingIdLocator) {
            callType = CallType.TEAMS_MEETING;
            final CallCompositeTeamsMeetingIdLocator teamsMeetingIdLocator =
                    (CallCompositeTeamsMeetingIdLocator) locator;
            meetingId = teamsMeetingIdLocator.getMeetingId();
            meetingPasscode = teamsMeetingIdLocator.getMeetingPasscode();
        } else if (locator instanceof CallCompositeTeamsMeetingLinkLocator) {
            callType = CallType.TEAMS_MEETING;
            meetingLink = ((CallCompositeTeamsMeetingLinkLocator) locator).getMeetingLink();
        } else if (locator instanceof CallCompositeRoomLocator) {
            callType = CallType.ROOMS_CALL;
            final CallCompositeRoomLocator roomLocator = (CallCompositeRoomLocator) locator;
            roomId = roomLocator.getRoomId();
        } else if (participants != null) {
            callType = CallType.ONE_TO_N_OUTGOING;
        } else if (incomingCallId != null) {
            callType = CallType.ONE_TO_ONE_INCOMING;
        } else {
            throw new CallCompositeException("Not supported Call type");
        }

        launchComposite(context,
                null,
                localOptions,
                isTest,
                groupId,
                meetingLink,
                meetingId,
                meetingPasscode,
                roomId,
                callType,
                participants,
                incomingCallId
        );
    }

    private void launchComposite(final Context context,
                                 final CallCompositeRemoteOptions remoteOptions,
                                 final CallCompositeLocalOptions localOptions,
                                 final boolean isTest,
                                 final UUID groupId,
                                 final String meetingLink,
                                 final String meetingId,
                                 final String meetingPasscode,
                                 final String roomId,
                                 final CallType callType,
                                 final Collection<CommunicationIdentifier> participants,
                                 final String incomingCallId) {
        configuration.setCallConfig(new CallConfiguration(
                groupId,
                meetingLink,
                meetingId,
                meetingPasscode,
                roomId,
                callType,
                participants,
                incomingCallId));

        configuration.setApplicationContext(context.getApplicationContext());
        if (remoteOptions != null) {
            configuration.setCredential(remoteOptions.getCredential());
            configuration.setDisplayName(remoteOptions.getDisplayName());
        }

        if (localOptions != null) {
            configuration.setCallCompositeLocalOptions(localOptions);
            // override builder provided options if they are provided in the localOptions
            if (localOptions.getSetupScreenOptions() != null) {
                configuration.setSetupScreenOptions(localOptions.getSetupScreenOptions());
            }
            if (localOptions.getCallScreenOptions() != null) {
                configuration.setCallScreenOptions(localOptions.getCallScreenOptions());
            }
        }

        initializeCallingSDK();

        diContainer = new DependencyInjectionContainerImpl(
                instanceId,
                context.getApplicationContext(),
                this,
                TestHelper.INSTANCE.getCallingSDK(),
                TestHelper.INSTANCE.getVideoStreamRendererFactory(),
                TestHelper.INSTANCE.getCoroutineContextProvider(),
                logger
        );

        showUI(context, isTest);
    }

    private void showUI(final Context context,
                        final boolean isTest) {

        /* <RTT_POC>
        // Simulates incoming RTT Message data for UI Dev
        new Timer().scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (Math.random() < 0.01) {
                            diContainer.getAppStore().dispatch(new RttAction.IncomingMessageReceived("Hello", "abc"));
                        }

                    }
                }, 0, 10);
        </RTT_POC> */

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

    private CallingSDKInitializer initializeCallingSDK() {
        if (sdkInitializer == null) {
            if (configuration.getApplicationContext() == null) {
                final String message = "Application context must be set.";
                throw new CallCompositeException(message, new IllegalArgumentException(message));
            }
            if (configuration.getCredential() == null) {
                final String message = "Credential must be set.";
                throw new CallCompositeException(message, new IllegalArgumentException(message));
            }
            sdkInitializer = new CallingSDKInitializer(logger, configuration);
        }
        return sdkInitializer;
    }

    CallingSDKInitializer getSdkInitialization() {
        return sdkInitializer;
    }

    CallCompositeConfiguration getConfiguration() {
        return this.configuration;
    }

    private void disposeSDKInitializer() {
        if (sdkInitializer != null) {
            sdkInitializer.dispose();
            sdkInitializer = null;
        }
    }

    void launchTest(final Context context,
                    final CallCompositeRemoteOptions remoteOptions,
                    final CallCompositeLocalOptions localOptions) {
        launchComposite(context, remoteOptions, localOptions, true);
    }

    void launchTest(final Context context,
                    final CallCompositeJoinLocator locator,
                    final CallCompositeLocalOptions localOptions) {
        launchComposite(context, locator, null, null, localOptions, true);
    }

    void launchTest(final Context context,
                    final List<CommunicationIdentifier> participants,
                    final CallCompositeLocalOptions localOptions) {
        launchComposite(context, null, participants, null, localOptions, true);
    }

    void launchTest(final Context context,
                    final String incomingCallId,
                    final CallCompositeLocalOptions localOptions) {
        launchComposite(context, null, null, incomingCallId, localOptions, true);
    }
}
