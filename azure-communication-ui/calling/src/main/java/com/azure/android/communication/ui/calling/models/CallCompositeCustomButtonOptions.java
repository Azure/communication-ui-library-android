// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

public final class CallCompositeCustomButtonOptions extends CallCompositeButtonOptions {
    private CallCompositeCustomButtonPlacement placement = CallCompositeCustomButtonPlacement.OVERFLOW;

    public CallCompositeButtonOptions setPlacement(final CallCompositeCustomButtonPlacement placement) {
        this.placement = placement;
        return this;
    }

    public CallCompositeCustomButtonPlacement getPlacement() {
        return this.placement;
    }
}
