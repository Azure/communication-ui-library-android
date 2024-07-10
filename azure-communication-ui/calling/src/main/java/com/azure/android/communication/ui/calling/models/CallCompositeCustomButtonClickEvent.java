// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

public final class CallCompositeCustomButtonClickEvent {
    private final Context context;
    private final CallCompositeCustomButtonOptions buttonOptions;

    CallCompositeCustomButtonClickEvent(final Context context, final CallCompositeCustomButtonOptions buttonOptions) {
        this.context = context;
        this.buttonOptions = buttonOptions;
    }

    public Context getContext() {
        return context;
    }

    public CallCompositeCustomButtonOptions getButtonOptions() {
        return buttonOptions;
    }
}
