// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

interface SupportedLanguageAttributes {
    String getLanguageCode(SupportedLanguages language);
    Boolean getIsRTLDefaultValue();
}
