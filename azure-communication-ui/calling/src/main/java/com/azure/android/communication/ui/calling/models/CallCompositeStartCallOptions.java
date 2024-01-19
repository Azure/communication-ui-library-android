// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

import java.util.List;

/**
 * Start Call options to start 1 to N call experience using {@link CallComposite}.
 */
final class CallCompositeStartCallOptions {
    private final List<String> participants;

    /**
     * Creates {@link CallCompositeStartCallOptions} using participants list.
     * @param participants List of participants.
     */
    public CallCompositeStartCallOptions(final List<String> participants) {
        this.participants = participants;
    }

    /**
     * Get participants list.
     * @return List of participants.
     */
    public List<String> getParticipants() {
        return participants;
    }
}
