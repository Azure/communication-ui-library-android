// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal fun buildCallCompositeAudioSelectionChangedEvent(mode: CallCompositeAudioSelectionMode): CallCompositeAudioSelectionChangedEvent {
    return CallCompositeAudioSelectionChangedEvent(mode)
}
