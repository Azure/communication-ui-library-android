// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;


import com.azure.android.communication.ui.CallComposite;

import java.util.List;
import java.util.Map;

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
    private final String languageCode;
    private int layoutDirection;
    private Map<String, String> customTranslation;

    /**
     * Create Localization Configuration with customString
     *
     * @param languageCode
     * @param layoutDirection layout direction eg,. LayoutDirection.RTL
     * @param customTranslation
     */
    public LocalizationConfiguration(final String languageCode, final int layoutDirection,
                                     final Map<String, String> customTranslation) {
        this.languageCode = languageCode;
        this.layoutDirection = layoutDirection;
        this.customTranslation = customTranslation;
    }

    /**
     * Create Localization configuration.
     *
     * @param languageCode    string eg,. "en"
     * @param layoutDirection layout direction eg,. LayoutDirection.RTL
     */
    public LocalizationConfiguration(final String languageCode, final int layoutDirection) {
        this.languageCode = languageCode;
        this.layoutDirection = layoutDirection;
    }

    /**
     * Create Localization configuration.
     *
     * @param languageCode string eg,. "en"
     */
    public LocalizationConfiguration(final String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Get current language String.
     *
     * @return language string
     */
    public String getLanguageCode() {
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
     * Get customTranslation Map
     *
     * @return customTranslation
     */
    public Map<String, String> getCustomTranslation() {
        return customTranslation;
    }

    /**
     * Get supported Locale string
     *
     * @return List of supported Locale as String List
     */
    public static List<String> getSupportedLanguages() {
        return AppLocalizationProvider.Companion.getSupportedLanguages();
    }
}
