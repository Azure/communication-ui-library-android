// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;

/**
 * Builder for creating {@link ChatAdapter}.
 *
 * <p>Used to build a {@link ChatAdapter} which is then used to start a chat.</p>
 * <p>This class can be used to specify a locale to be used by the Chat Composite</p>
 */
public final class ChatAdapterBuilder {

    private String endpoint;
    private CommunicationIdentifier identity;
    private CommunicationTokenCredential credential;
    private String displayName;

    public ChatAdapterBuilder endpoint(final String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public ChatAdapterBuilder identity(final CommunicationIdentifier identity) {
        this.identity = identity;
        return this;
    }

    public ChatAdapterBuilder credential(final CommunicationTokenCredential credential) {
        this.credential = credential;
        return this;
    }

    public ChatAdapterBuilder displayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Builds the {@link ChatAdapter} class.
     *
     * @return {@link ChatAdapter}
     */
    public ChatAdapter build() {
        final ChatCompositeConfiguration config = new ChatCompositeConfiguration();
        return new ChatAdapter(config, endpoint, identity, credential, displayName);
    }
}
