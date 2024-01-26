// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionChangedEvent

class AudioSelectionEventHandler(private val handler: CallCompositeEventsHandler) :
    CallCompositeEventHandler<CallCompositeAudioSelectionChangedEvent> {
    override fun handle(event: CallCompositeAudioSelectionChangedEvent) {
        handler.onAudioSelectionChanged(event)
    }
}
