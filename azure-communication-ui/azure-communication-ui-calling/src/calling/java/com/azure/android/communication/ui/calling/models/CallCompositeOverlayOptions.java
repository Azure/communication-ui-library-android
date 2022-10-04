// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

public final class CallCompositeOverlayOptions {

    private Boolean showPiP;
    private CallCompositePiPViewOptions callCompositePiPViewOptions;

    public CallCompositeOverlayOptions() {

    }

    public Boolean getShowPiP() {
        return showPiP;
    }

    public CallCompositeOverlayOptions setShowPiP(final Boolean showPiP) {
        this.showPiP = showPiP;
        return this;
    }

    public CallCompositePiPViewOptions getCallCompositePiPViewOptions() {
        return callCompositePiPViewOptions;
    }

    public CallCompositeOverlayOptions setCallCompositePiPViewOptions(
            final CallCompositePiPViewOptions callCompositePiPViewOptions) {
        this.callCompositePiPViewOptions = callCompositePiPViewOptions;
        return this;
    }
}
