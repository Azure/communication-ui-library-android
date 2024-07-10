// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

public class CallCompositeButtonClickEvent {
    private final Context context;
    private final CallCompositeButtonOptions buttonOptions;

    CallCompositeButtonClickEvent(final Context context, final CallCompositeButtonOptions buttonOptions) {
        this.context = context;
        this.buttonOptions = buttonOptions;
    }

    public Context getContext() {
        return context;
    }

    public CallCompositeButtonOptions getButtonOptions() {
        return buttonOptions;
    }
}
