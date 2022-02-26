// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

public enum SupportedLanguages {
    ENGLISH("en", false),
    CHINESE_SIMPLIFIED("zh-CN", false),
    SPANISH("es", false),
    RUSSIAN("ru", false),
    JAPANESE("ja", false),
    FRENCH("fr", false),
    BRAZILIAN_PORTUGUESE("pt-BR", false),
    GERMAN("de", false),
    KOREAN("ko", false),
    ITALIAN("it", false),
    CHINESE_TRADITIONAL("zh-TW", false),
    DUTCH("nl", false),
    TURKISH("tr", false),
    ENGLISH_UK("en-GB", false);

    private final String languageCode;
    private final Boolean isRTLDefaultValue;

    SupportedLanguages(final String languageCode, final Boolean isRTLDefaultValue) {
        this.languageCode = languageCode;
        this.isRTLDefaultValue = isRTLDefaultValue;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public Boolean getIsRTLDefaultValue() {
        return this.isRTLDefaultValue;
    }
}

