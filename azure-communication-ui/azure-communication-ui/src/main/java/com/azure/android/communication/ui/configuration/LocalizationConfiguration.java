// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;


import com.azure.android.communication.ui.CallComposite;

/**
 * Localization configuration to provide for CallComposite.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder with different parameters
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .localization&#40;new LocalizationConfiguration&#40;LanguageCode.FRENCH&#41;&#41;;
 *
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .localization&#40;new LocalizationConfiguration&#40;LanguageCode.FRENCH, layoutDirection&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallComposite
 */
public final class LocalizationConfiguration {
    private final LanguageCode languageCode;
    private int layoutDirection;

    /**
     * Create Localization configuration.
     *
     * Set {@link LanguageCode};.
     *
     * @param languageCode    The {@link LanguageCode}; eg,. LanguageCode.FRENCH
     * @param layoutDirection layout direction eg,. LayoutDirection.RTL
     */
    public LocalizationConfiguration(final LanguageCode languageCode, final int layoutDirection) {
        this.languageCode = languageCode;
        this.layoutDirection = layoutDirection;
    }

    /**
     * Create Localization configuration.
     *
     * @param languageCode The {@link LanguageCode}; eg,. LanguageCode.FRENCH
     */
    public LocalizationConfiguration(final LanguageCode languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Get current LanguageCode enum
     *
     * @return The {@link LanguageCode};
     */
    public LanguageCode getLanguageCode() {
        return languageCode;
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
