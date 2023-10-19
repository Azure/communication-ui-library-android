// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.List;

public class CallCompositeStartCallOptions {
    private final List<String> participants;

    public CallCompositeStartCallOptions(final List<String> participants) {
        this.participants = participants;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
