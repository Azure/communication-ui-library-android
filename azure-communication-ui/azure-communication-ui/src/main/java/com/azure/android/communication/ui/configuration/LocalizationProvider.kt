// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

// Localization Provider applies the localization configuration in the library system
// Makes the AppLocalizationProvider scalable for further feature addition
// LocalizationConfiguration contains the locale, customStrings and RTL settings options.
// For implementation
// @see {@link com.azure.android.communication.ui.configuration.AppLocalizationProvider}
internal interface LocalizationProvider {
    fun apply(localeConfig: LocalizationConfiguration)
    fun getLocalizedString(localeKey: String, sdkLocale: String): String
}
