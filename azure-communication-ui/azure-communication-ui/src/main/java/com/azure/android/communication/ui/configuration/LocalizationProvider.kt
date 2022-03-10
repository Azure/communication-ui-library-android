// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

internal interface LocalizationProvider {
    fun apply(localeConfig: LocalizationConfiguration)
    fun getLocalizedString(localeKey: String, sdkLocale: String): String
}
