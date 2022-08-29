// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;

public class ChatComposite {

    private final ChatCompositeConfiguration configuration;

    ChatComposite(final ChatCompositeConfiguration configuration) {
        this.configuration = configuration;
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
     * Launch group chat composite in the background.
     *
     * @param remoteOptions The {@link ChatCompositeRemoteOptions} has remote parameters to
     *                      launch group chat experience.
     * @param localOptions  The {@link ChatCompositeLocalOptions} has local parameters to
     *                      launch group chat experience.
     */
    public void launchInBackground(ChatCompositeRemoteOptions remoteOptions, ChatCompositeLocalOptions localOptions) {

    }

    private void launchComposite(final Context context,
                                 final ChatCompositeRemoteOptions remoteOptions,
                                 final ChatCompositeLocalOptions localOptions,
                                 final boolean isTest) {
    }


    /**
     * Set {@link ChatCompositeRemoteOptions}.
     *
     * <p>
     * Used to set Participant View Data (E.g. Avatar and displayName) to be used on this device only.
     * </p>
     *
     * @param participantViewData The {@link ChatCompositeRemoteOptions}.
     * @param identifier          The {@link CommunicationIdentifier}.
     */
    public void setChatCompositeRemoteOptions(ChatCompositeRemoteOptions participantViewData, final CommunicationIdentifier identifier) {
    }
}
