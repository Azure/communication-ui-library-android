// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

public enum SupportedLanguages {
    CHINESE_SIMPLIFIED("zh-CN"),
    SPANISH("es"),
    RUSSIAN("ru"),
    JAPANESE("ja"),
    FRENCH("fr"),
    BRAZILIAN_PORTUGUESE("pt-BR"),
    GERMAN("de"),
    KOREAN("ko"),
    ITALIAN("it"),
    CHINESE_TRADITIONAL("zh-TW"),
    DUTCH("nl"),
    TURKISH("tr"),
    ENGLISH_UK("en-GB");

    private final String languageCode;

    SupportedLanguages(final String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }
}

