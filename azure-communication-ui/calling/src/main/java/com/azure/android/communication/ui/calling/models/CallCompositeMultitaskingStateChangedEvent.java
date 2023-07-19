// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 *
 */
public final class CallCompositeMultitaskingStateChangedEvent {
    private final boolean isMultitasking;

    public CallCompositeMultitaskingStateChangedEvent(final boolean isMultitasking) {
        this.isMultitasking = isMultitasking;
    }

    public boolean isMultitasking() {
        return this.isMultitasking;
    }
}
