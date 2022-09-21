// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import java.util.Locale;

public final class CallWithChatCompositeLocalizationOptions {
    private Integer layoutDirection;
    private final Locale locale;

    /**
     * Create Localization configuration.
     *
     * @param locale The {@link Locale}; eg,. {@code Locale.US}
     */
    public CallWithChatCompositeLocalizationOptions(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Create Localization configuration.
     *
     * @param locale          The {@link Locale}; eg,. {@code Locale.US}
     * @param layoutDirection layout direction int; eg,. {@code LayoutDirection.RTL}
     */
    public CallWithChatCompositeLocalizationOptions(final Locale locale, final int layoutDirection) {
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
