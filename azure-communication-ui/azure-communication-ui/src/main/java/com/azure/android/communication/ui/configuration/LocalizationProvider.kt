// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

// Localization Provider applies the localization configuration in the library system
//
// @method {@apply Applies the custom locale settings received from LocalizationConfiguration}
// @method (@getLocalizedString Provides the string value for a specific key}
//
// For implementation
//
// @see {@link com.azure.android.communication.ui.configuration.AppLocalizationProvider}
internal interface LocalizationProvider {
    fun apply(localeConfig: LocalizationConfiguration)
}
