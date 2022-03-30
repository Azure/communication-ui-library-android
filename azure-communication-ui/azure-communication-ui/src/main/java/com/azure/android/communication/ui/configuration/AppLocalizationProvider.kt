// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import android.content.Context

internal class AppLocalizationProvider : LocalizationProvider {
    private lateinit var language: String
    private var customTranslation: Map<String, String>? = null

    override fun apply(localeConfig: LocalizationConfiguration) {
        val supportedLocales = LanguageCode.values()
        if (supportedLocales.contains(localeConfig.languageCode)) {
            language = localeConfig.languageCode.toString()
        }
    }

    override fun getLocalizedString(localeKey: String, sdkLocale: String): String {
        var localizedString = sdkLocale
        customTranslation?.let {
            if (it.containsKey(localeKey)) {
                localizedString = it[localeKey]!!
            }
        }

        return localizedString
    }

    override fun getLocalizedString(context: Context, stringKey: Int): String {
        var localizedString = context.getString(stringKey)
        val localeKey = context.resources.getResourceEntryName(stringKey)
        customTranslation?.let {
            if (it.containsKey(localeKey)) {
                localizedString = it[localeKey]!!
            }
        }

        return localizedString
    }
}
