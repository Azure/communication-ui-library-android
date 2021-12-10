// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import android.content.Context;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.configuration.CallConfiguration;
import com.azure.android.communication.ui.configuration.CallType;
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode;
import com.azure.android.communication.ui.configuration.events.ErrorEvent;
import com.azure.android.communication.ui.di.DIContainerHolderKt;
import com.azure.android.communication.ui.di.DependencyInjectionContainer;
import com.azure.android.communication.ui.di.DependencyInjectionContainerImpl;
import com.azure.android.communication.ui.presentation.UIManager;

import org.jetbrains.annotations.NotNull;

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
 *     .theme&#40;new ThemeConfiguration&#40;themeId&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallCompositeBuilder
 */
public final class CallComposite {

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
     * @param groupCallOptions  The {@link GroupCallOptions} has parameters to
     *                          launch group call experience.
     *
     */
    public void launch(final GroupCallOptions groupCallOptions) {
        launch(
                groupCallOptions.getContext(),
                groupCallOptions.getCommunicationTokenCredential(),
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
     * @param teamsMeetingOptions The {@link TeamsMeetingOptions} has parameters to
     *                            launch Teams meeting experience.
     */
    public void launch(final TeamsMeetingOptions teamsMeetingOptions) {
        launch(
                teamsMeetingOptions.getContext(),
                teamsMeetingOptions.getCommunicationTokenCredential(),
                teamsMeetingOptions.getDisplayName(),
                null,
                teamsMeetingOptions.getMeetingLink(),
                CallType.TEAMS_MEETING
        );
    }

    /**
     * Set {@link CallingEventHandler}&lt;{@link ErrorEvent}&gt;.
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
     * @param eventHandler The {@link CallingEventHandler}&lt;{@link ErrorEvent}&gt;
     */
    public void setOnErrorHandler(final CallingEventHandler<ErrorEvent<CallCompositeErrorCode>> eventHandler) {
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

        final UIManager uiManager = getUIManager(configuration, context);
        uiManager.start();
    }

    @NotNull
    private DependencyInjectionContainer initDI(
            final CallCompositeConfiguration configuration,
            final Context parentContext
    ) {
        final DependencyInjectionContainer di = new DependencyInjectionContainerImpl(configuration, parentContext);
        DIContainerHolderKt.setDIContainer(di);
        return di;
    }

    @NotNull
    private UIManager getUIManager(
            final CallCompositeConfiguration configuration,
            final Context context
    ) {
        final DependencyInjectionContainer di = initDI(configuration, context);
        return di.provideUIManager();
    }
}
