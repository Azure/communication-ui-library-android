// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

/**
 * Call composite custom button on click event.
 */
public final class CallCompositeCustomButtonClickEvent {
    private final Context context;
    private final CallCompositeCustomButtonOptions buttonOptions;

    CallCompositeCustomButtonClickEvent(final Context context, final CallCompositeCustomButtonOptions buttonOptions) {
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
     * @return {@link CallCompositeCustomButtonOptions}
     */
    public CallCompositeCustomButtonOptions getButtonOptions() {
        return buttonOptions;
    }
}
