// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public class SupportedLanguages extends ExpandableStringEnum<SupportedLanguages> {

    public static final SupportedLanguages ENGLISH = fromString("en");
    public static final SupportedLanguages CHINESE_SIMPLIFIED = fromString("zh-CN");
    public static final SupportedLanguages SPANISH = fromString("es");
    public static final SupportedLanguages RUSSIAN = fromString("ru");
    public static final SupportedLanguages JAPANESE = fromString("ja");
    public static final SupportedLanguages FRENCH = fromString("fr");
    public static final SupportedLanguages BRAZILIAN_PORTUGUESE = fromString("pt-BR");
    public static final SupportedLanguages GERMAN = fromString("de");
    public static final SupportedLanguages KOREAN = fromString("ko");
    public static final SupportedLanguages ITALIAN = fromString("it");
    public static final SupportedLanguages CHINESE_TRADITIONAL = fromString("zh-TW");
    public static final SupportedLanguages DUTCH = fromString("nl");
    public static final SupportedLanguages TURKISH = fromString("tr");
    public static final SupportedLanguages ENGLISH_UK = fromString("en-GB");

    /**
     * Creates or finds a SupportedLanguages from its string representation.
     *
     * @param languageCode is the locale.
     * @return the corresponding SupportedLanguages.
     */
    public static SupportedLanguages fromString(final String languageCode) {
        return fromString(languageCode, SupportedLanguages.class);
    }

    /**
     * Gets the collection of all known enum values
     *
     * @return collection of all enums
     */
    public static Collection<SupportedLanguages> values() {
        return values(SupportedLanguages.class);
    }
}

