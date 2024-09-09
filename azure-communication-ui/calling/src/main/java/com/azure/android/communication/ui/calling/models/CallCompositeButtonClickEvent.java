// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

/**
 * Call composite button on click event.
 */
public final class CallCompositeButtonClickEvent {
    private final Context context;
    private final CallCompositeButtonViewData button;

    CallCompositeButtonClickEvent(final Context context, final CallCompositeButtonViewData button) {
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
     * @return {@link CallCompositeButtonViewData}
     */
    public CallCompositeButtonViewData getButton() {
        return button;
    }
}
