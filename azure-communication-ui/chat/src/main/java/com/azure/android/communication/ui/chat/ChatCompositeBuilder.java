// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;

/**
 * Builder for creating {@link ChatComposite}.
 *
 * <p>Used to build a {@link ChatComposite} which is then used to start a chat.</p>
 * <p>This class can be used to specify a locale to be used by the Chat Composite</p>
 */
public final class ChatCompositeBuilder {

    private ChatCompositeLocalizationOptions localizationConfig = null;
    private ChatCompositeRemoteOptions remoteOptions;
    private ChatCompositeLocalOptions localOptions;
    private Context context;

    /**
     * Sets an optional localization for chat-composite to use by {@link ChatComposite}.
     *
     * @param localization {@link ChatCompositeLocalizationOptions}.
     * @return {@link ChatCompositeBuilder} for chaining options
     */
    public ChatCompositeBuilder localization(final ChatCompositeLocalizationOptions localization) {
        this.localizationConfig = localization;
        return this;
    }

    /**
     * The android context used to create the composite.
     * @param context
     * @return
     */
    public ChatCompositeBuilder context(final Context context) {
        this.context = context;
        return this;
    }

    /**
     * The {@link ChatCompositeRemoteOptions} has remote parameters to connect to chat thread.
     * @param remoteOptions
     * @return
     */
    public ChatCompositeBuilder remoteOptions(final ChatCompositeRemoteOptions remoteOptions) {
        this.remoteOptions = remoteOptions;
        return this;
    }

    /**
     * The {@link ChatCompositeLocalOptions} has local parameters for ChatComposite.
     * @param localOptions
     * @return
     */
    public ChatCompositeBuilder localOptions(final ChatCompositeLocalOptions localOptions) {
        this.localOptions = localOptions;
        return this;
    }

    /**
     * Builds the {@link ChatComposite} class.
     *
     * @return {@link ChatComposite}
     */
    public ChatComposite build() {
        final ChatCompositeConfiguration config = new ChatCompositeConfiguration();
        config.setLocalizationConfig(localizationConfig);
        return new ChatComposite(context, config, localOptions, remoteOptions);
    }
}
