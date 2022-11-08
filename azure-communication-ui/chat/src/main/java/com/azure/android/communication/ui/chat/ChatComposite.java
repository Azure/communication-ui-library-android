// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import static com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifierKt.into;

import android.content.Context;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeParticipantViewData;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent;


/**
 * Azure android communication chat composite component.
 *
 * <p><strong>Instantiating Chat Composite</strong></p>
 */
public final class ChatComposite {

    private static int instanceIdCounter = 0;
    private final ChatContainer chatContainer;
    private final ChatCompositeLocalOptions localOptions;
    private final ChatCompositeRemoteOptions remoteOptions;
    private final Context context;
    private final ChatCompositeConfiguration configuration;
    final Integer instanceId = instanceIdCounter++;

    ChatComposite(final Context context,
                  final ChatCompositeConfiguration configuration,
                  final ChatCompositeLocalOptions localOptions,
                  final ChatCompositeRemoteOptions remoteOptions) {
        this.context = context;
        this.configuration = configuration;
        chatContainer = new ChatContainer(this, configuration, instanceId);

        this.localOptions = localOptions;
        this.remoteOptions = remoteOptions;
    }

    /**
     * Connects to backend services.
     */
    public void connect() {
        launchComposite(context, remoteOptions, localOptions, false);
    }

    /**
     * Add {@link ChatCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.chat.models.ChatCompositeErrorCode} for values.</p>
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
     * @param errorHandler The {@link ChatCompositeEventHandler}.
     */
    public void addOnErrorEventHandler(final ChatCompositeEventHandler<ChatCompositeErrorEvent> errorHandler) {
    }

    /**
     * Remove {@link ChatCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent} for values.</p>
     *
     * @param errorHandler The {@link ChatCompositeEventHandler}.
     */
    public void removeOnErrorEventHandler(final ChatCompositeEventHandler<ChatCompositeErrorEvent> errorHandler) {
    }

    /**
     * Add {@link ChatCompositeEventHandler} with {@link ChatCompositeUnreadMessageChangedEvent}.
     *
     * @param handler The {@link ChatCompositeEventHandler}.
     */
    public void addOnUnreadMessagesChangedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent> handler) {
    }

    /**
     * Remove {@link ChatCompositeEventHandler} with {@link ChatCompositeUnreadMessageChangedEvent}.
     *
     * @param handler The {@link ChatCompositeEventHandler}.
     */
    public void removeOnUnreadMessagesChangedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent> handler) {

    }

    /**
     * Set {@link ChatCompositeParticipantViewData}.
     *
     * <p>
     * Used to set Participant View Data (E.g. Avatar and displayName) to be used on this device only.
     * </p>
     *
     * @param identifier          The {@link CommunicationIdentifier}.
     * @param participantViewData The {@link ChatCompositeParticipantViewData}.
     * @return {@link ChatCompositeSetParticipantViewDataResult}.
     */
    public ChatCompositeSetParticipantViewDataResult setRemoteParticipantViewData(
            final CommunicationIdentifier identifier,
            final ChatCompositeParticipantViewData participantViewData) {
        return configuration.getRemoteParticipantsConfiguration()
                .setParticipantViewData(into(identifier), participantViewData);
    }

    private void launchComposite(final Context context,
                                 final ChatCompositeRemoteOptions remoteOptions,
                                 final ChatCompositeLocalOptions localOptions,
                                 final boolean isTest) {
        chatContainer.start(context, remoteOptions, localOptions);
    }

    // TODO: remove this method. Test should provide it's own UI host and call
    //  launch() then getCompositeUIView()
    void launchTest(final Context context,
                    final ChatCompositeRemoteOptions remoteOptions,
                    final ChatCompositeLocalOptions localOptions) {
        chatContainer.start(context, remoteOptions, localOptions);
        //showTestCompositeUI(context);
    }
}
