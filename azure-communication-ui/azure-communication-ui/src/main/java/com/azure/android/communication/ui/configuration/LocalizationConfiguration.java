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
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .customizeLocalization&#40;new LocalizationConfiguration&#40;language&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallComposite
 */
public class LocalizationConfiguration {
    private String language;
    private boolean isRightToLeft;
    private Map<String, String> customString;

    /**
     * Create Localization Configuration with customString
     *
     * @param language
     * @param isRightToLeft
     * @param customString
     */
    public LocalizationConfiguration(final String language, final boolean isRightToLeft,
                                     final Map<String, String> customString) {
        this.language = language;
        this.isRightToLeft = isRightToLeft;
        this.customString = customString;
    }

    /**
     * Create Localization configuration.
     *
     * @param language      string eg,. "en"
     * @param isRightToLeft boolean the layout direction
     */
    public LocalizationConfiguration(final String language, final boolean isRightToLeft) {
        this.language = language;
        this.isRightToLeft = isRightToLeft;
    }

    /**
     * Create Localization configuration.
     *
     * @param language string eg,. "en"
     */
    public LocalizationConfiguration(final String language) {
        this.language = language;
    }

    /**
     * Get current language String.
     *
     * @return language string
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get isRightToLeft boolean.
     *
     * @return isRightToLeft boolean
     */
    public boolean isRightToLeft() {
        return isRightToLeft;
    }

    /**
     * Get customTranslation Map
     *
     * @return customTranslation
     */
    public Map<String, String> getCustomString() {
        return customString;
    }

    /**
     * Get supported Locale string
     *
     * @return List of supported Locale as String List
     */
    public List<String> getSupportedLanguages() {
        return AppLocalizationProvider.Companion.getSupportedLanguages();
    }
}
