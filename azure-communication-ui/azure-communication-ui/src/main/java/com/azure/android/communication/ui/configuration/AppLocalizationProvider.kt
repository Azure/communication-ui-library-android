// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

internal class AppLocalizationProvider : LocalizationProvider {
    private lateinit var language: String
    private var customTranslation: Map<String, String>? = null

    override fun apply(localeConfig: LocalizationConfiguration) {
        val supportedLocales = getSupportedLocales()
        if (supportedLocales.contains(localeConfig.language)) {
            language = localeConfig.language
        }
        if (localeConfig.customTranslation != null) {
            customTranslation = localeConfig.customTranslation
        }
    }

    override fun getLocalizedString(localeKey: String, sdkLocale: String): String {
        var localizedString = sdkLocale
        if (customTranslation?.containsKey(localeKey) == true) {
            localizedString = customTranslation!![localeKey]!!
        }
        return localizedString
    }

    companion object {

        fun getSupportedLanguages(): List<String> {
            return SupportedLanguages.values()
        }

        fun getSupportedLocales(): List<String> {
            return SupportedLanguages.values().map { SupportedLanguages.getLanguageCode(it) }
        }
    }
}
