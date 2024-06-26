// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsErrors
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType

internal data class CaptionsState(
    val isEnabled: Boolean = false,
    val isStarted: Boolean = false,
    val supportedSpokenLanguages: List<String> = emptyList(),
    val activeSpokenLanguage: String = "",
    val supportedCaptionLanguages: List<String> = emptyList(),
    val activeCaptionLanguage: String = "",
    val isTranslationSupported: Boolean = false,
    val activeType: CallCompositeCaptionsType = CallCompositeCaptionsType.NONE,
    val errors: CallCompositeCaptionsErrors = CallCompositeCaptionsErrors.NONE,
    val showCaptionsOptions: Boolean = false,
    val showSupportedSpokenLanguages: Boolean = false,
    val showSupportedCaptionLanguages: Boolean = false,
)
