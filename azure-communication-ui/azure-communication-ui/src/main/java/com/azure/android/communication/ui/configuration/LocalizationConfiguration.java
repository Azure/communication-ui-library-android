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
    private String languageCode;
    private boolean isRightToLeft;
    private Map<String, String> customTranslation;

    /**
     * Create Localization Configuration with customString
     *
     * @param languageCode
     * @param isRightToLeft
     * @param customTranslation
     */
    public LocalizationConfiguration(final String languageCode, final boolean isRightToLeft,
                                     final Map<String, String> customTranslation) {
        this.languageCode = languageCode;
        this.isRightToLeft = isRightToLeft;
        this.customTranslation = customTranslation;
    }

    /**
     * Create Localization configuration.
     *
     * @param languageCode  string eg,. "en"
     * @param isRightToLeft boolean the layout direction
     */
    public LocalizationConfiguration(final String languageCode, final boolean isRightToLeft) {
        this.languageCode = languageCode;
        this.isRightToLeft = isRightToLeft;
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
    public boolean isRightToLeft() {
        return isRightToLeft;
    }

    /**
     * Get customTranslation Map
     *
     * @return customTranslation
     */
    public Map<String, String> getCustomTranslation() {
        return customTranslation;
    }
}
