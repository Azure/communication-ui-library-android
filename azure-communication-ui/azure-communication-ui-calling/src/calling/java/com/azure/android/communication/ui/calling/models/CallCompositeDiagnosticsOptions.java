// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.Collection;

public final class CallCompositeDiagnosticsOptions {

    private final Collection<String> tags;

    public CallCompositeDiagnosticsOptions(final Collection<String> tags) {

        this.tags = tags;
    }

    public Collection<String> getTags() {
        return tags;
    }
}
