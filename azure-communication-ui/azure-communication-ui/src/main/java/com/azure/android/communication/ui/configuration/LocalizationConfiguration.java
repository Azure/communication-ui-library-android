// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;


import com.azure.android.communication.ui.CallComposite;

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
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *      .localization&#40;new LocalizationConfiguration&#40;"fr", "FR"&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallComposite
 */
public final class LocalizationConfiguration {
    private int layoutDirection;
    private final Locale locale;

    /**
     * Create Localization configuration.
     *
     * @param locale          The {@link Locale}; eg,. Locale.US
     * @param layoutDirection layout direction eg,. LayoutDirection.RTL
     */
    public LocalizationConfiguration(final Locale locale, final int layoutDirection) {
        this.locale = locale;
        this.layoutDirection = layoutDirection;
    }

    /**
     * Create Localization configuration.
     *
     * @param language language String; eg,. "fr"
     * @param country  country String; eg,. "FR"
     */
    public LocalizationConfiguration(final String language, final String country) {
        this.locale = new Locale(language, country);
    }

    /**
     * Create Localization configuration.
     *
     * @param language language String; eg,. "fr"
     */
    public LocalizationConfiguration(final String language) {
        this.locale = new Locale(language);
    }

    /**
     * Create Localization configuration.
     *
     * @param locale The {@link Locale}; eg,. Locale.US
     */
    public LocalizationConfiguration(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Get current LanguageCode enum
     *
     * @return The {@link Locale};
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get layoutDirection int.
     *
     * @return layoutDirection int
     */
    public int getLayoutDirection() {
        return layoutDirection;
    }
}
