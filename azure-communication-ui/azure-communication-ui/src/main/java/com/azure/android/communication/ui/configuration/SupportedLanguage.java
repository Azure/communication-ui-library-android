// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines locale or languageCode for each supported language
 */
public final class SupportedLanguage extends ExpandableStringEnum<SupportedLanguage> {

    public static final SupportedLanguage EN = fromString("en");
    public static final SupportedLanguage EN_US = fromString("en-US");
    public static final SupportedLanguage EN_UK = fromString("en-GB");
    public static final SupportedLanguage ZH_CN = fromString("zh-CN");
    public static final SupportedLanguage ZH_TW = fromString("zh-TW");
    public static final SupportedLanguage ES = fromString("es");
    public static final SupportedLanguage ES_ES = fromString("es-ES");
    public static final SupportedLanguage RU = fromString("ru");
    public static final SupportedLanguage RU_RU = fromString("ru-RU");
    public static final SupportedLanguage JA = fromString("ja");
    public static final SupportedLanguage JA_JP = fromString("ja-JP");
    public static final SupportedLanguage FR = fromString("fr");
    public static final SupportedLanguage FR_FR = fromString("fr-FR");
    public static final SupportedLanguage PT = fromString("pt");
    public static final SupportedLanguage PT_BR = fromString("pt-BR");
    public static final SupportedLanguage DE = fromString("de");
    public static final SupportedLanguage DE_DE = fromString("de-DE");
    public static final SupportedLanguage KO = fromString("ko");
    public static final SupportedLanguage KO_KR = fromString("ko-KR");
    public static final SupportedLanguage IT = fromString("it");
    public static final SupportedLanguage IT_IT = fromString("it-IT");
    public static final SupportedLanguage NL = fromString("nl");
    public static final SupportedLanguage NL_NL = fromString("nl-NL");
    public static final SupportedLanguage TR = fromString("tr");
    public static final SupportedLanguage TR_TR = fromString("tr-TR");

    /**
     * Creates or finds a SupportedLanguages from its string representation.
     *
     * @param languageCode is the locale in string eg,. "en"
     * @return the corresponding SupportedLanguages
     */
    private static SupportedLanguage fromString(final String languageCode) {
        return fromString(languageCode, SupportedLanguage.class);
    }

    /**
     * Gets the collection of supported languages
     *
     * @return collection of all supported locale values
     */
    public static Collection<SupportedLanguage> values() {
        return values(SupportedLanguage.class);
    }
}

