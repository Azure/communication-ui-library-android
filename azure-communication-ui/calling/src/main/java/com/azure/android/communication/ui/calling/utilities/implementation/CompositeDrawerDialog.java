// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities.implementation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.azure.android.communication.ui.calling.implementation.R;
import com.microsoft.fluentui.drawer.DrawerDialog;

public class CompositeDrawerDialog extends DrawerDialog {
    private final int contentDescription;

    public CompositeDrawerDialog(
            final @NonNull Context context, final @NonNull BehaviorType behaviorType, final int contentDescription) {
        super(context, behaviorType);
        this.contentDescription = contentDescription;
        initOnShow();
    }

    @Override
    public void setOnShowListener(final @Nullable OnShowListener listener) {
        super.setOnShowListener(dialog -> {
            onShow();
            if (listener != null) {
                listener.onShow(dialog);
            }
        });
    }

    private void initOnShow() {
        super.setOnShowListener(dialog -> {
            onShow();
        });
    }

    private void onShow() {
        // Not a great solution to use the CoordinatorLayout, but it's the only way to set the content description.
        // The issue is posted to the FluentUI library: https://github.com/microsoft/fluentui-android/issues/758
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.drawer_container);

        if (coordinatorLayout != null) {
            coordinatorLayout.setContentDescription(getContext().getString(contentDescription));
        }
    }
}
