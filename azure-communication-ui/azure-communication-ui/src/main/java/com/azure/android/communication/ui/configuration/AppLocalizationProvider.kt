// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

internal class AppLocalizationProvider: LocalizationProvider {
    private lateinit var language: String
    private lateinit var customString: Map<String, String>

    override fun apply(localeConfig: LocalizationConfiguration) {
        val supportedLocales = getSupportedLanguages()
        //if(supportedLocales.contains(localeConfig.language)) {
            language = localeConfig.language
            customString = localeConfig.customString
        //}
    }

    override fun getLocalizedString(localeKey: String, sdkLocale: String): String {
        if (customString?.containsKey(localeKey)) {
            return customString[localeKey]!!
        } else return sdkLocale
    }

    companion object {

        fun getSupportedLanguages(): List<String> {
            return SupportedLanguages.values().map { it.toString() }
        }
    }
}