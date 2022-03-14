// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

// Supported Language attributes enables the extensible enum to have public method properties
// For Implementation
// @see {@link com.azure.android.communication.ui.configuration}
internal interface SupportedLanguageAttributes {
    fun getLanguageCode(language: SupportedLanguages): String
    fun getIsRTLDefaultValue(language: SupportedLanguages): Boolean
}
