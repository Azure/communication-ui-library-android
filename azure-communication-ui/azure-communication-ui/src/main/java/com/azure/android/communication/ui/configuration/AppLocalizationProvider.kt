// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

internal class AppLocalizationProvider : LocalizationProvider {
    private lateinit var language: String
    private var customTranslation: Map<String, String>? = null

    override fun apply(localeConfig: LocalizationConfiguration) {
        val supportedLocales = LanguageCode.values()
        if (supportedLocales.contains(localeConfig.languageCode)) {
            language = localeConfig.languageCode.toString()
        }
    }
}
