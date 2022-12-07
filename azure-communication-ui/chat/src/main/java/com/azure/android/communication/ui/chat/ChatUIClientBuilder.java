// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;

/**
 * Builder for creating {@link ChatUIClient}.
 *
 * <p>Used to build a {@link ChatUIClient} which is then used to start a chat.</p>
 * <p>This class can be used to specify a locale to be used by the Chat Composite</p>
 */
public final class ChatUIClientBuilder {

    private String endpoint;
    private CommunicationIdentifier identity;
    private CommunicationTokenCredential credential;
    private String displayName;
    private Context applicationContext;

    /**
     * Sets Azure Communication Service endpoint.
     * @param endpoint
     * @return
     */
    public ChatUIClientBuilder endpoint(final String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * Sets Azure Communication identity.
     * @param identity
     * @return
     */
    public ChatUIClientBuilder identity(final CommunicationIdentifier identity) {
        this.identity = identity;
        return this;
    }

    /**
     * Sets Azure Communication credential
     * @param credential
     * @return
     */
    public ChatUIClientBuilder credential(final CommunicationTokenCredential credential) {
        this.credential = credential;
        return this;
    }

    /**
     * Sets chat participant display name
     * @param displayName
     * @return
     */
    public ChatUIClientBuilder displayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets android context.
     * @param context
     * @return
     */
    public ChatUIClientBuilder context(final Context context) {
        this.applicationContext = context.getApplicationContext();
        return this;
    }

    /**
     * Builds the {@link ChatUIClient} class.
     *
     * @return {@link ChatUIClient}
     */
    public ChatUIClient build() {
        final ChatCompositeConfiguration config = new ChatCompositeConfiguration();
        return new ChatUIClient(applicationContext, config, endpoint, identity, credential, displayName);
    }
}
