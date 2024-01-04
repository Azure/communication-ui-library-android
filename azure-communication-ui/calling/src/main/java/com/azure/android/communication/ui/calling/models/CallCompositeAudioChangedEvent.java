// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

public class CallCompositeAudioChangedEvent {
    private final CallCompositeAudioDevice device;

    public CallCompositeAudioChangedEvent(final CallCompositeAudioDevice device) {
        this.device = device;
    }

    public CallCompositeAudioDevice getDevice() {
        return device;
    }
}
