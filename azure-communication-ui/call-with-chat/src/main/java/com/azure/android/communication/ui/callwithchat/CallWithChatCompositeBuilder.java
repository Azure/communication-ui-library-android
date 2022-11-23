// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat;

import com.azure.android.communication.ui.calling.CallComposite;
import com.azure.android.communication.ui.calling.CallCompositeBuilder;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalizationOptions;
import com.azure.android.communication.ui.callwithchat.service.CallWithChatService;
import com.azure.android.communication.ui.chat.ChatAdapter;
import com.azure.android.communication.ui.chat.ChatAdapterBuilder;

public final class CallWithChatCompositeBuilder {

    private Integer themeConfig = null;
    private CallWithChatCompositeLocalizationOptions localizationConfig = null;

    /**
     * Sets an optional theme for call-composite to use by {@link CallWithChatComposite}.
     *
     * @param themeId Theme ID.
     * @return {@link CallWithChatCompositeBuilder} for chaining options
     */
    public CallWithChatCompositeBuilder theme(final int themeId) {
        this.themeConfig = themeId;
        return this;
    }

    /**
     * Sets an optional localization for call-composite to use by {@link CallWithChatComposite}.
     *
     * @param localization {@link CallWithChatCompositeLocalizationOptions}.
     * @return {@link CallWithChatCompositeBuilder} for chaining options
     */
    public CallWithChatCompositeBuilder localization(
            final CallWithChatCompositeLocalizationOptions localization) {
        this.localizationConfig = localization;
        return this;
    }



    /**
     * Builds the {@link CallWithChatComposite}.
     *
     * @return {@link CallWithChatComposite}
     */
    public CallWithChatComposite build() {
        final ChatAdapterBuilder chatAdapterBuilder = new ChatAdapterBuilder();
        final ChatAdapter chatAdapter = chatAdapterBuilder.build();

        final CallCompositeBuilder callCompositeBuilder = new CallCompositeBuilder();


        final CallComposite callComposite = callCompositeBuilder
                .build();

        return new CallWithChatComposite(new CallWithChatService(callComposite, chatAdapter));
    }
}
