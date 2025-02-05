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

    public CompositeDrawerDialog(@NonNull Context context, @NonNull BehaviorType behaviorType, int contentDescription) {
        super(context, behaviorType);
        this.contentDescription = contentDescription;
        initOnShow();
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
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
        CoordinatorLayout coordinatorLayout = findViewById(R.id.drawer_container);

        if (coordinatorLayout != null) {
            coordinatorLayout.setContentDescription(getContext().getString(contentDescription));
        }
    }
}
