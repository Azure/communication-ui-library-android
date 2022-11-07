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
public class ChatComposite {

    private static int instanceIdCounter = 0;
    private final ChatContainer chatContainer;
    private final ChatCompositeLocalOptions localOptions;
    private final ChatCompositeRemoteOptions remoteOptions;
    private final Context context;
    private final ChatCompositeConfiguration configuration;
    final Integer instanceId = instanceIdCounter++;
//    private Runnable closeUIRequestHandler;

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

    public void connect() {
        launchComposite(context, remoteOptions, localOptions, false);
    }

//    /**
//     * Launch group chat composite.
//     *
//     * @param context       The android context used to start the Composite.
//     * @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
//     *                      launch group chat experience.
//     */
//    public void launch(final Context context, final ChatCompositeRemoteOptions remoteOptions) {
//        launch(context, remoteOptions, null);
//    }
//
//    /**
//     * Launch group chat composite.
//     *
//     * @param context       The android context used to start the Composite.
//     * @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
//     *                      launch group chat experience.
//     * @param localOptions  The {@link ChatCompositeLocalOptions} has local parameters to
//     *                      launch group chat experience.
//     */
//    public void launch(final Context context,
//                       final ChatCompositeRemoteOptions remoteOptions,
//                       final ChatCompositeLocalOptions localOptions) {
//        launchComposite(context, remoteOptions, localOptions, false);
//    }

//    /**
//     * Stop the ChatComposite. Destroy the UI if in foreground mode. Destroy service layer.
//     */
//    public void stop() {
//        chatContainer.stop();
//    }

//    /**
//     * Get Composite UI view
//     *
//     * @param context The android context used to start the Composite.\
//     * @return View ChatComposite UI view
//     */
//    public View getCompositeUIView(final Context context) {
//        return new ChatView(context, instanceId, closeUIRequestHandler);
//    }

//    /**
//     * Add {@link ChatCompositeEventHandler}.
//     *
//     * @param handler The {@link ChatCompositeEventHandler}.
//     */
//    public void addOnCompositeViewCloseRequestedEventHandler(final ChatCompositeEventHandler<Object> handler) {
//        closeUIRequestHandler = new Runnable() {
//            @Override
//            public void run() {
//                handler.handle(null);
//            }
//        };
//    }
//
//    /**
//     * Remove {@link ChatCompositeEventHandler}.
//     *
//     * @param handler The {@link ChatCompositeEventHandler}.
//     */
//    public void removeOnViewClosedEventHandler(final ChatCompositeEventHandler<Object> handler) {
//    }

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
