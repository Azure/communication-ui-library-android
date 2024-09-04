// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

/**
 * Call composite custom button on click event.
 */
public final class CallCompositeCustomButtonClickEvent {
    private final Context context;
    private final CallCompositeCustomButtonViewData button;

    CallCompositeCustomButtonClickEvent(final Context context, final CallCompositeCustomButtonViewData button) {
        this.context = context;
        this.button = button;
    }

    /**
     * Get context.
     * @return {@link Context}
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get button.
     * @return {@link CallCompositeCustomButtonViewData}
     */
    public CallCompositeCustomButtonViewData getButton() {
        return button;
    }
}
