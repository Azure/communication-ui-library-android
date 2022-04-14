// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import java.util.Locale

internal class AppLocalizationProvider : LocalizationProvider {
    private lateinit var locale: Locale
    override fun apply(localeConfig: LocalizationConfiguration) {
        locale = localeConfig.locale
    }
}
