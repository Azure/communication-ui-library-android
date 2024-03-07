// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with audio selection changed.
 */
public final class CallCompositeAudioSelectionChangedEvent {
    private final CallCompositeAudioSelectionType selectionType;

    /**
     * Create {@link CallCompositeAudioSelectionChangedEvent} with selectionType.
     *
     * @param selectionType selection type  {@link CallCompositeAudioSelectionType}.
     */
    public CallCompositeAudioSelectionChangedEvent(final CallCompositeAudioSelectionType selectionType) {
        this.selectionType = selectionType;
    }

    /**
     * Get audio selection type.
     *
     * @return the {@link CallCompositeAudioSelectionType}.
     */
    public CallCompositeAudioSelectionType getSelectionType() {
        return selectionType;
    }
}
