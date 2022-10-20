// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import static com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifierKt.into;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeParticipantViewData;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeActivity;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatView;


/**
 * Azure android communication chat composite component.
 *
 * <p><strong>Instantiating Chat Composite</strong></p>
 */
public class ChatComposite {

    private static int instanceIdCounter = 0;
    private final ChatContainer chatContainer;
    private final ChatCompositeConfiguration configuration;
    private final Integer instanceId = instanceIdCounter++;

    ChatComposite(final ChatCompositeConfiguration configuration) {
        this.configuration = configuration;
        chatContainer = new ChatContainer(this, configuration, instanceId);
    }

    /**
     * Launch group chat composite.
     *
     * @param context       The android context used to start the Composite.
     * @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
     *                      launch group chat experience.
     */
    public void launch(final Context context, final ChatCompositeRemoteOptions remoteOptions) {
        launch(context, remoteOptions, null);
    }

    /**
     * Launch group chat composite.
     *
     * @param context       The android context used to start the Composite.
     * @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
     *                      launch group chat experience.
     * @param localOptions  The {@link ChatCompositeLocalOptions} has local parameters to
     *                      launch group chat experience.
     */
    public void launch(final Context context,
                       final ChatCompositeRemoteOptions remoteOptions,
                       final ChatCompositeLocalOptions localOptions) {
        launchComposite(context, remoteOptions, localOptions, false);
    }

    /**
     * Stop the ChatComposite. Destroy the UI if in foreground mode. Destroy service layer.
     */
    public void stop() {
        chatContainer.stop();
    }

    /**
     * Get Composite UI view
     *
     * @param context The android context used to start the Composite.\
     * @return View ChatComposite UI view
     */
    public View getCompositeUIView(final Context context) {
        return new ChatView(context, instanceId);
    }

    /**
     * To show full composite default view
     *
     * @param context The android context used to start the Composite.
     */
    public void showCompositeUI(final Context context) {
        final Intent launchIntent = new Intent(context, ChatCompositeActivity.class);
        launchIntent.putExtra(ChatCompositeActivity.KEY_INSTANCE_ID, instanceId);
        context.startActivity(launchIntent);
    }

    /**
     * To hide full composite view
     *
     * @param context The android context used to start the Composite.
     */
    public void hideCompositeUI(final Context context) {
    }

    /**
     * Add {@link ChatCompositeEventHandler}.
     *
     * @param handler The {@link ChatCompositeEventHandler}.
     */
    public void addOnViewClosedEventHandler(final ChatCompositeEventHandler<Object> handler) {
    }

    /**
     * Remove {@link ChatCompositeEventHandler}.
     *
     * @param handler The {@link ChatCompositeEventHandler}.
     */
    public void removeOnViewClosedEventHandler(final ChatCompositeEventHandler<Object> handler) {
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
        if (localOptions.getIsLaunchingWithUI()) {
            showCompositeUI(context);
        }
    }

    void launchTest(final Context context,
                    final ChatCompositeRemoteOptions remoteOptions,
                    final ChatCompositeLocalOptions localOptions) {
        chatContainer.start(context, remoteOptions, localOptions);
        showTestCompositeUI(context);
    }

   private void showTestCompositeUI(final Context context) {
       final Intent launchIntent = new Intent(context, ChatCompositeActivity.class);
       launchIntent.putExtra(ChatCompositeActivity.KEY_INSTANCE_ID, instanceId);
       launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       context.startActivity(launchIntent);
   }
}
