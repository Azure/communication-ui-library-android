// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

internal interface SupportedLanguageAttributes {
    fun getLanguageCode(language: SupportedLanguages): String
    fun getIsRTLDefaultValue(language: SupportedLanguages): Boolean
}
