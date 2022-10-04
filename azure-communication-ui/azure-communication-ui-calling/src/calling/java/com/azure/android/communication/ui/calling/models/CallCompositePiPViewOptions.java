// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.graphics.Rect;

public final class CallCompositePiPViewOptions {
    private CallCompositePiPViewDefaultPosition defaultPosition = CallCompositePiPViewDefaultPosition.TOP_RIGHT;
    private Boolean isDraggable = true;
    private Rect pipDraggableAreaMargins = new Rect(0, 0, 0, 0);

    public CallCompositePiPViewDefaultPosition getDefaultPosition() {
        return defaultPosition;
    }

    public CallCompositePiPViewOptions setDefaultPosition(final CallCompositePiPViewDefaultPosition defaultPosition) {
        this.defaultPosition = defaultPosition;
        return this;
    }

    public Boolean getDraggable() {
        return isDraggable;
    }

    public CallCompositePiPViewOptions setDraggable(final Boolean draggable) {
        isDraggable = draggable;
        return this;
    }

    public Rect getPipDraggableAreaMargins() {
        return pipDraggableAreaMargins;
    }

    public CallCompositePiPViewOptions setPipDraggableAreaMargins(final Rect pipDraggableAreaMargins) {
        this.pipDraggableAreaMargins = pipDraggableAreaMargins;
        return this;
    }
}
