// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines locale or languageCode for each supported language
 */
public final class LanguageCode extends ExpandableStringEnum<LanguageCode> {

    public static final LanguageCode ENGLISH_US = fromString("en");
    public static final LanguageCode CHINESE_SIMPLIFIED = fromString("zh-CN");
    public static final LanguageCode SPANISH = fromString("es");
    public static final LanguageCode RUSSIAN = fromString("ru");
    public static final LanguageCode JAPANESE = fromString("ja");
    public static final LanguageCode FRENCH = fromString("fr");
    public static final LanguageCode BRAZILIAN_PORTUGUESE = fromString("pt-BR");
    public static final LanguageCode GERMAN = fromString("de");
    public static final LanguageCode KOREAN = fromString("ko");
    public static final LanguageCode ITALIAN = fromString("it");
    public static final LanguageCode CHINESE_TRADITIONAL = fromString("zh-TW");
    public static final LanguageCode DUTCH = fromString("nl");
    public static final LanguageCode TURKISH = fromString("tr");
    public static final LanguageCode ENGLISH_UK = fromString("en-GB");

    /**
     * Creates or finds a SupportedLanguages from its string representation.
     *
     * @param languageCode is the locale in string eg,. "en"
     * @return the corresponding SupportedLanguages
     */
    private static LanguageCode fromString(final String languageCode) {
        return fromString(languageCode, LanguageCode.class);
    }

    /**
     * Gets the collection of supported languages
     *
     * @return collection of all supported locale values
     */
    public static Collection<LanguageCode> values() {
        return values(LanguageCode.class);
    }
}

