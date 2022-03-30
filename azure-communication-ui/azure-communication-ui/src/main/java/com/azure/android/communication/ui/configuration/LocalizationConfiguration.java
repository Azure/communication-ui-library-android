// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;


import com.azure.android.communication.ui.CallComposite;

import java.util.List;

/**
 * Localization configuration to provide for CallComposite.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder with different parameters
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .customizeLocalization&#40;new LocalizationConfiguration&#40;languageCode&#41;&#41;;
 *
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .customizeLocalization&#40;new LocalizationConfiguration&#40;languageCode, layoutDirection&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallComposite
 */
public class LocalizationConfiguration {
    private final LanguageCode languageCode;
    private int layoutDirection;

    /**
     * Create Localization configuration.
     *
     * @param languageCode    string eg,. "en"
     * @param layoutDirection layout direction eg,. LayoutDirection.RTL
     */
    public LocalizationConfiguration(final LanguageCode languageCode, final int layoutDirection) {
        this.languageCode = languageCode;
        this.layoutDirection = layoutDirection;
    }

    /**
     * Create Localization configuration.
     *
     * @param languageCode string eg,. "en"
     */
    public LocalizationConfiguration(final LanguageCode languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Get current language String.
     *
     * @return language string
     */
    public LanguageCode getLanguageCode() {
        return languageCode;
    }

    /**
     * Get isRightToLeft boolean.
     *
     * @return isRightToLeft boolean
     */
    public int layoutDirection() {
        return layoutDirection;
    }

    /**
     * Get supported Locale string
     *
     * @return List of supported Locale as String List
     */
    public static List<LanguageCode> getSupportedLanguages() {
        return AppLocalizationProvider.Companion.getSupportedLanguages();
    }
}
