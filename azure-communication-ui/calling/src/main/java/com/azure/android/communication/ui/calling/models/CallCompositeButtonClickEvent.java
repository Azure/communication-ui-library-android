// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

/**
 * Call composite button on click event.
 */
public final class CallCompositeButtonClickEvent {
    private final Context context;
    private final CallCompositeButtonOptions buttonOptions;

    CallCompositeButtonClickEvent(final Context context, final CallCompositeButtonOptions buttonOptions) {
        this.context = context;
        this.buttonOptions = buttonOptions;
    }

    /**
     * Get context.
     * @return {@link Context}
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get button options.
     * @return {@link CallCompositeButtonOptions}
     */
    public CallCompositeButtonOptions getButtonOptions() {
        return buttonOptions;
    }
}
