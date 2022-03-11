// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

internal class AppLocalizationProvider : LocalizationProvider {
    private lateinit var language: String
    private var customString: Map<String, String>? = null

    override fun apply(localeConfig: LocalizationConfiguration) {
        val supportedLocales = getSupportedLocales()
        if (supportedLocales.contains(localeConfig.language)) {
            language = localeConfig.language
            customString = localeConfig.customString
        }
    }

    override fun getLocalizedString(localeKey: String, sdkLocale: String): String {
        var localizedString = sdkLocale
        if (customString?.containsKey(localeKey) == true) {
            localizedString = customString!![localeKey]!!
        }
        return localizedString
    }

    companion object {

        fun getSupportedLanguages(): List<String> {
            return SupportedLanguages.values().map { it.toString() }
        }

        fun getSupportedLocales(): List<String> {
            return SupportedLanguages.values().map { it.getLanguageCode(it) }
        }
    }
}
