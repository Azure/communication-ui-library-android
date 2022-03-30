// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public class LanguageCode extends ExpandableStringEnum<LanguageCode> {

    public static final LanguageCode ENGLISH = fromString("en");
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
     * @param languageCode is the locale.
     * @return the corresponding SupportedLanguages.
     */
    public static LanguageCode fromString(final String languageCode) {
        return fromString(languageCode, LanguageCode.class);
    }

    /**
     * Gets the collection of all known enum values
     *
     * @return collection of all enums
     */
    public static Collection<LanguageCode> values() {
        return values(LanguageCode.class);
    }
}

