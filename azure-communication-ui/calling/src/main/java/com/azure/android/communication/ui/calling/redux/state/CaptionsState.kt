// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsErrors
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType

internal data class CaptionsState(
    val isCaptionsUIEnabled: Boolean = false,
    val isCaptionsStarted: Boolean = false,
    val supportedSpokenLanguages: List<String> = emptyList(),
    val activeSpokenLanguage: String = "",
    val supportedCaptionLanguages: List<String> = emptyList(),
    val activeCaptionLanguage: String = "",
    val isTranslationSupported: Boolean = false,
    val activeType: CallCompositeCaptionsType = CallCompositeCaptionsType.NONE,
    val lastCaptionsError: CaptionsError? = null,
    val showCaptionsToggleUI: Boolean = false,
    val showSupportedSpokenLanguagesSelection: Boolean = false,
    val showSupportedCaptionLanguagesSelections: Boolean = false,
)

internal enum class CaptionsErrorType {
    CAPTIONS_START_ERROR,
    CAPTIONS_STOP_ERROR,
    CAPTIONS_SPOKEN_LANGUAGE_UPDATE_ERROR,
    CAPTIONS_CAPTION_LANGUAGE_UPDATE_ERROR
}

internal data class CaptionsError(
    val error: CallCompositeCaptionsErrors,
    val errorType: CaptionsErrorType
)
