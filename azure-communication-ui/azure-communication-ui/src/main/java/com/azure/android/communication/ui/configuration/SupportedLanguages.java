// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SupportedLanguages {
    private static final List<String> SUPPORTED_LANGUAGE_LIST = new ArrayList<String>();
    private static final String ENGLISH = "ENGLISH";
    private static final String CHINESE_SIMPLIFIED = "CHINESE_SIMPLIFIED";
    private static final String SPANISH = "SPANISH";
    private static final String RUSSIAN = "RUSSIAN";
    private static final String JAPANESE = "JAPANESE";
    private static final String FRENCH = "FRENCH";
    private static final String BRAZILIAN_PORTUGUESE = "BRAZILIAN_PORTUGUESE";
    private static final String GERMAN = "GERMAN";
    private static final String KOREAN = "KOREAN";
    private static final String ITALIAN = "ITALIAN";
    private static final String CHINESE_TRADITIONAL = "CHINESE_TRADITIONAL";
    private static final String DUTCH = "DUTCH";
    private static final String TURKISH = "TURKISH";
    private static final String ENGLISH_UK = "ENGLISH_UK";

    static {
        SUPPORTED_LANGUAGE_LIST.add(ENGLISH);
        SUPPORTED_LANGUAGE_LIST.add(CHINESE_SIMPLIFIED);
        SUPPORTED_LANGUAGE_LIST.add(SPANISH);
        SUPPORTED_LANGUAGE_LIST.add(RUSSIAN);
        SUPPORTED_LANGUAGE_LIST.add(JAPANESE);
        SUPPORTED_LANGUAGE_LIST.add(FRENCH);
        SUPPORTED_LANGUAGE_LIST.add(BRAZILIAN_PORTUGUESE);
        SUPPORTED_LANGUAGE_LIST.add(GERMAN);
        SUPPORTED_LANGUAGE_LIST.add(KOREAN);
        SUPPORTED_LANGUAGE_LIST.add(ITALIAN);
        SUPPORTED_LANGUAGE_LIST.add(CHINESE_TRADITIONAL);
        SUPPORTED_LANGUAGE_LIST.add(DUTCH);
        SUPPORTED_LANGUAGE_LIST.add(TURKISH);
        SUPPORTED_LANGUAGE_LIST.add(ENGLISH_UK);
    }

    /** @return known SupportedLanguages as String list. */
    public static List<String> values() {
        return Collections.unmodifiableList(SUPPORTED_LANGUAGE_LIST);
    }

    /**
     * Gets the locale language code for the supported language
     *
     * @param language is a supported language.
     * @return corresponding locale value for the language
     */
    public static String getLanguageCode(final String language) {
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
    public static boolean getIsRTLDefaultValue(final String language) {
        return false;
    }
}

