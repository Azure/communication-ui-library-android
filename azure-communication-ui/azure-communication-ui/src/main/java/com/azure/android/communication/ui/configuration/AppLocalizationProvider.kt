// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import android.content.Context

internal class AppLocalizationProvider : LocalizationProvider {
    private lateinit var language: String
    private var customTranslation: Map<String, String>? = null

    override fun apply(localeConfig: LocalizationConfiguration) {
        val supportedLocales = getSupportedLocales()
        if (supportedLocales.contains(localeConfig.languageCode)) {
            language = localeConfig.languageCode
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

    override fun getLocalizedString(context: Context, stringKey: Int): String {
        var localizedString = context.getString(stringKey)
        val localeKey = context.resources.getResourceEntryName(stringKey)
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
