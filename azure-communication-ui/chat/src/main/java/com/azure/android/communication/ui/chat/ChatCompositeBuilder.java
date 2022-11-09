// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions;

/**
 * Builder for creating {@link ChatComposite}.
 *
 * <p>Used to build a {@link ChatComposite} which is then used to start a chat.</p>
 * <p>This class can be used to specify a locale to be used by the Chat Composite</p>
 */
public final class ChatCompositeBuilder {

    private ChatCompositeLocalizationOptions localizationConfig = null;

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
     * Builds the {@link ChatComposite} class.
     *
     * @return {@link ChatComposite}
     */
    public ChatComposite build() {
        final ChatCompositeConfiguration config = new ChatCompositeConfiguration();
        config.setLocalizationConfig(localizationConfig);
        return new ChatComposite(config);
    }
}
