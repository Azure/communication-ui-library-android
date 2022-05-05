// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration;

import androidx.annotation.NonNull;

import com.azure.android.communication.ui.calling.CallComposite;

import java.util.Locale;

/**
 * Localization configuration to provide for CallComposite.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder with different parameters
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .localization&#40;new LocalizationConfiguration&#40;Locale.CHINESE&#41;&#41;;
 *
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .localization&#40;new LocalizationConfiguration&#40;Locale.CHINESE, LayoutDirection.RTL&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallComposite
 */
public final class LocalizationConfiguration {
    private Integer layoutDirection;
    private final Locale locale;

    /**
     * Create Localization configuration.
     *
     * @param locale The {@link Locale}; eg,. Locale.US
     */
    public LocalizationConfiguration(@NonNull final Locale locale) {
        this.locale = locale;
    }

    /**
     * Create Localization configuration.
     *
     * @param locale          The {@link Locale}; eg,. Locale.US
     * @param layoutDirection layout direction int; eg,. LayoutDirection.RTL
     */
    public LocalizationConfiguration(@NonNull final Locale locale, final int layoutDirection) {
        this.locale = locale;
        this.layoutDirection = layoutDirection;
    }

    /**
     * Get current {@link Locale}.
     *
     * @return The {@link Locale}
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get layoutDirection {@link Integer}.
     *
     * @return layoutDirection {@link Integer}
     */
    public Integer getLayoutDirection() {
        return layoutDirection;
    }
}
