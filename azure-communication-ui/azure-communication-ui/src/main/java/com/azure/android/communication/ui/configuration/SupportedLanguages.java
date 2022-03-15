// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;
import java.util.Collection;

final class SupportedLanguages extends ExpandableStringEnum<SupportedLanguages>
        implements SupportedLanguageAttributes {
    public static final SupportedLanguages ENGLISH = fromString("ENGLISH");
    public static final SupportedLanguages CHINESE_SIMPLIFIED = fromString("CHINESE_SIMPLIFIED");
    public static final SupportedLanguages SPANISH = fromString("SPANISH");
    public static final SupportedLanguages RUSSIAN = fromString("RUSSIAN");
    public static final SupportedLanguages JAPANESE = fromString("JAPANESE");
    public static final SupportedLanguages FRENCH = fromString("FRENCH");
    public static final SupportedLanguages BRAZILIAN_PORTUGUESE = fromString("BRAZILIAN_PORTUGUESE");
    public static final SupportedLanguages GERMAN = fromString("GERMAN");
    public static final SupportedLanguages KOREAN = fromString("KOREAN");
    public static final SupportedLanguages ITALIAN = fromString("ITALIAN");
    public static final SupportedLanguages CHINESE_TRADITIONAL = fromString("CHINESE_TRADITIONAL");
    public static final SupportedLanguages DUTCH = fromString("DUTCH");
    public static final SupportedLanguages TURKISH = fromString("TURKISH");
    public static final SupportedLanguages ENGLISH_UK = fromString("ENGLISH_UK");

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
     * Gets the enum instance name in String
     *
     * @return the corresponding enum instance in string
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /** @return known SupportedLanguages values. */
    public static Collection<SupportedLanguages> values() {
        return values(SupportedLanguages.class);
    }

    /**
     * Gets the locale language code for the supported language
     *
     * @param language is a supported language.
     * @return corresponding locale value for the language
     */
    @Override
    public String getLanguageCode(final String language) {
        switch (language) {
            case "SPANISH": return "es";
            case "CHINESE_SIMPLIFIED": return "zh-CN";
            case "RUSSIAN": return "ru";
            case "JAPANESE": return "ja";
            case "FRENCH": return "fr";
            case "BRAZILIAN_PORTUGUESE": return "pt-BR";
            case "GERMAN": return "de";
            case "KOREAN": return "ko";
            case "ITALIAN": return "it";
            case "CHINESE_TRADITIONAL": return "zh-TW";
            case "DUTCH": return "nl";
            case "TURKISH": return "tr";
            case "ENGLISH_UK": return "en-GB";
            default: return "en";
        }
    }


    /**
     * Gets the default isRTL config
     *
     * @return returns the default or conventional isRTl boolean value
     */
    @Override
    public boolean getIsRTLDefaultValue(final String language) {
        return false;
    }
}

