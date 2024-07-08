// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType

internal enum class CaptionsStatus {
    START_REQUESTED,
    STARTED,
    STOP_REQUESTED,
    STOPPED,
    NONE
}

internal data class CaptionsState(
    val isCaptionsUIEnabled: Boolean = false,
    val status: CaptionsStatus = CaptionsStatus.NONE,
    val supportedSpokenLanguages: List<String> = emptyList(),
    val activeSpokenLanguage: String = "",
    val supportedCaptionLanguages: List<String> = emptyList(),
    val activeCaptionLanguage: String = "",
    val isTranslationSupported: Boolean = false,
    val activeType: CallCompositeCaptionsType = CallCompositeCaptionsType.NONE,
    val showCaptionsToggleUI: Boolean = false,
    val showSupportedSpokenLanguagesSelection: Boolean = false,
    val showSupportedCaptionLanguagesSelections: Boolean = false,
)
