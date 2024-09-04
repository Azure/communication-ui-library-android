// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

/**
 * Call composite button on click event.
 */
public final class CallCompositeButtonClickEvent {
    private final Context context;
    private final CallCompositeButtonViewData buttonOptions;

    CallCompositeButtonClickEvent(final Context context, final CallCompositeButtonViewData buttonOptions) {
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
     * @return {@link CallCompositeButtonViewData}
     */
    public CallCompositeButtonViewData getButtonOptions() {
        return buttonOptions;
    }
}
