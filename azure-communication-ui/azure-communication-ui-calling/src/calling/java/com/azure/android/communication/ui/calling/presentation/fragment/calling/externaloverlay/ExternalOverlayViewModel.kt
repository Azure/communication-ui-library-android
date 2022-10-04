// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.externaloverlay

import com.azure.android.communication.ui.calling.models.CallCompositeOverlayBuilder
import com.azure.android.communication.ui.calling.redux.state.ExternalOverlayState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ExternalOverlayViewModel {
    private val _externalViewFlow = MutableStateFlow<CallCompositeOverlayBuilder?>(null)
    internal val externalViewFlow: StateFlow<CallCompositeOverlayBuilder?> = _externalViewFlow

    fun init(
        externalOverlayState: ExternalOverlayState,
    ) {
        _externalViewFlow.value = externalOverlayState.externalOverlayViewBuilder
    }

    fun update(
        externalOverlayState: ExternalOverlayState,
    ) {
        _externalViewFlow.value = externalOverlayState.externalOverlayViewBuilder
    }
}
