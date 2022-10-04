// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions;

public class ChatCompositeBuilder {
    private ChatCompositeLocalizationOptions localizationConfig = null;

    /**
     * Builds the {@link ChatComposite}.
     *
     * @return {@link ChatComposite}
     */
    public ChatComposite build() {

        final ChatCompositeConfiguration config = new ChatCompositeConfiguration(localizationConfig);
        return new ChatComposite(config);
    }

    public ChatCompositeBuilder localization(final ChatCompositeLocalizationOptions localization) {
        this.localizationConfig = localization;
        return this;
    }
}
