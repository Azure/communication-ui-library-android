// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with audio selection changed.
 */
public final class CallCompositeAudioSelectionChangedEvent {
    private final CallCompositeAudioSelectionMode selectionType;

    /**
     * Create {@link CallCompositeAudioSelectionChangedEvent} with selection.
     *
     * @param selectionType selection type  {@link CallCompositeAudioSelectionMode}.
     */
    public CallCompositeAudioSelectionChangedEvent(final CallCompositeAudioSelectionMode selectionType) {
        this.selectionType = selectionType;
    }

    /**
     * Get selection type.
     *
     * @return the {@link CallCompositeAudioSelectionMode}.
     */
    public CallCompositeAudioSelectionMode getSelectionType() {
        return selectionType;
    }
}
