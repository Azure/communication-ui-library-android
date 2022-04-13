// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines locale or languageCode for each supported language
 */
public final class SupportLanguage extends ExpandableStringEnum<SupportLanguage> {

    public static final SupportLanguage ENGLISH_US = fromString("en");
    public static final SupportLanguage CHINESE_SIMPLIFIED = fromString("zh-CN");
    public static final SupportLanguage SPANISH = fromString("es");
    public static final SupportLanguage RUSSIAN = fromString("ru");
    public static final SupportLanguage JAPANESE = fromString("ja");
    public static final SupportLanguage FRENCH = fromString("fr");
    public static final SupportLanguage BRAZILIAN_PORTUGUESE = fromString("pt-BR");
    public static final SupportLanguage GERMAN = fromString("de");
    public static final SupportLanguage KOREAN = fromString("ko");
    public static final SupportLanguage ITALIAN = fromString("it");
    public static final SupportLanguage CHINESE_TRADITIONAL = fromString("zh-TW");
    public static final SupportLanguage DUTCH = fromString("nl");
    public static final SupportLanguage TURKISH = fromString("tr");
    public static final SupportLanguage ENGLISH_UK = fromString("en-GB");

    /**
     * Creates or finds a SupportedLanguages from its string representation.
     *
     * @param languageCode is the locale in string eg,. "en"
     * @return the corresponding SupportedLanguages
     */
    private static SupportLanguage fromString(final String languageCode) {
        return fromString(languageCode, SupportLanguage.class);
    }

    /**
     * Gets the collection of supported languages
     *
     * @return collection of all supported locale values
     */
    public static Collection<SupportLanguage> values() {
        return values(SupportLanguage.class);
    }
}

