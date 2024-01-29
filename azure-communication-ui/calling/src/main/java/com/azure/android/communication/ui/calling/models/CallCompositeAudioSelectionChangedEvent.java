// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with audio selection changed.
 */
public final class CallCompositeAudioSelectionChangedEvent {
    private final String selectionType;
    /**
     * Create {@link CallCompositeAudioSelectionChangedEvent} with selection.
     *
     * @param selectionType selection type.
     */
    public CallCompositeAudioSelectionChangedEvent(final String selectionType) {
        this.selectionType = selectionType;
    }

    /**
     * Returns the selection type.
     *
     * @return the selection type.
     */
    public String getSelectionType() {
        return selectionType;
    }
}
