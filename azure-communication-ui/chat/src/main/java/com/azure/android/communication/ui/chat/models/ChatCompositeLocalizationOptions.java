// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.android.communication.ui.chat.models;

import androidx.annotation.NonNull;

import com.azure.android.communication.ui.chat.ChatManager;

import java.util.Locale;

/**
 * Localization configuration to provide for {@link ChatManager}.
 *
 * @see ChatManager
 */
public final class ChatCompositeLocalizationOptions {
    private final Locale locale;
    private Integer layoutDirection;

    /**
     * Create Localization configuration.
     *
     * @param locale The {@link Locale}; eg,. {@code Locale.US}
     */
    public ChatCompositeLocalizationOptions(@NonNull final Locale locale) {
        this.locale = locale;
    }

    /**
     * Create Localization configuration.
     *
     * @param locale          The {@link Locale}; eg,. {@code Locale.US}
     * @param layoutDirection layout direction int; eg,. {@code LayoutDirection.RTL}
     */
    public ChatCompositeLocalizationOptions(@NonNull final Locale locale, final int layoutDirection) {
        this.locale = locale;
        this.layoutDirection = layoutDirection;
    }

    /**
     * Get current {@link Locale}.
     *
     * @return The {@link Locale}.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get layoutDirection {@link Integer}.
     *
     * @return layoutDirection {@link Integer}.
     */
    public Integer getLayoutDirection() {
        return layoutDirection;
    }
}
