// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import static com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifierKt.into;

import android.content.Context;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeParticipantViewData;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeSetParticipantViewDataResult;


/**
 * Azure android communication chat manager component.
 *
 * This allows you to connect to the SDK, listen for events and connect to "Threads".
 *
 * <p><strong>Instantiating Chat Composite</strong></p>
 */
public class ChatManager {
    private final Context context;
    private final ChatCompositeConfiguration configuration;



    ChatManager(final Context context, final ChatCompositeConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;

    }

    /**
     * Launch group chat composite.
     *
     * @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
     *                      launch group chat experience.
     * @return
     */
    public ChatThreadManager connectToChatThread(final ChatCompositeRemoteOptions remoteOptions) {
        return connectToChatThread(remoteOptions, null);
    }

    /**
     * Connect to a Thread.
     *  @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
     *                      launch group chat experience.
     * @param localOptions  The {@link ChatCompositeLocalOptions} has local parameters to
     * @return
     */
    public ChatThreadManager connectToChatThread(
                       final ChatCompositeRemoteOptions remoteOptions,
                       final ChatCompositeLocalOptions localOptions) {
        return startThread(context, remoteOptions, localOptions, false);
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

    ChatThreadManager startThread(final Context context,
                             final ChatCompositeRemoteOptions remoteOptions,
                             final ChatCompositeLocalOptions localOptions,
                             final boolean isTest) {
        final ChatThreadManager result = new ChatThreadManager(new ChatThreadContainer(this, configuration));
        result.start(context, remoteOptions, localOptions);
        return result;
    }

    //-----------------------------------------
    /*
    @Deprecated
    void launchTest(final Context context,
                    final ChatCompositeRemoteOptions remoteOptions,
                    final ChatCompositeLocalOptions localOptions) {
        chatThreadContainer.start(context, remoteOptions, localOptions);
        showTestCompositeUI(context);
    }

    @Deprecated
    private void showTestCompositeUI(final Context context) {
        final Intent launchIntent = new Intent(context, ChatCompositeActivity.class);
        launchIntent.putExtra(ChatCompositeActivity.KEY_INSTANCE_ID, instanceId);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }

     */
}
