// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines locale or languageCode for each supported language
 */
public final class SupportLanguage extends ExpandableStringEnum<SupportLanguage> {

    public static final SupportLanguage EN = fromString("en");
    public static final SupportLanguage EN_US = fromString("en-US");
    public static final SupportLanguage EN_UK = fromString("en-GB");
    public static final SupportLanguage ZH_CN = fromString("zh-CN");
    public static final SupportLanguage ZH_TW = fromString("zh-TW");
    public static final SupportLanguage ES = fromString("es");
    public static final SupportLanguage ES_ES = fromString("es-ES");
    public static final SupportLanguage RU = fromString("ru");
    public static final SupportLanguage RU_RU = fromString("ru-RU");
    public static final SupportLanguage JA = fromString("ja");
    public static final SupportLanguage JA_JP = fromString("ja-JP");
    public static final SupportLanguage FR = fromString("fr");
    public static final SupportLanguage FR_FR = fromString("fr-FR");
    public static final SupportLanguage PT = fromString("pt");
    public static final SupportLanguage PT_BR = fromString("pt-BR");
    public static final SupportLanguage DE = fromString("de");
    public static final SupportLanguage DE_DE = fromString("de-DE");
    public static final SupportLanguage KO = fromString("ko");
    public static final SupportLanguage KO_KR = fromString("ko-KR");
    public static final SupportLanguage IT = fromString("it");
    public static final SupportLanguage IT_IT = fromString("it-IT");
    public static final SupportLanguage NL = fromString("nl");
    public static final SupportLanguage NL_NL = fromString("nl-NL");
    public static final SupportLanguage TR = fromString("tr");
    public static final SupportLanguage TR_TR = fromString("tr-TR");

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

