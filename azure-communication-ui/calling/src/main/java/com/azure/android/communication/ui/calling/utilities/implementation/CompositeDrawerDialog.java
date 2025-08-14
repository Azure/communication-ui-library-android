// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities.implementation;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

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
        final View drawer = findViewById(R.id.drawer_container);
        if (drawer.getContext().getApplicationInfo().targetSdkVersion >= 35) {
            final RecyclerView recyclerView = findViewById(R.id.bottom_drawer_table);
            if (recyclerView == null) {
                return;
            }

            ViewCompat.setOnApplyWindowInsetsListener(drawer, (view, windowInsets) -> {
                final Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

                final int orientation = view.getResources().getConfiguration().orientation;
                final boolean isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT;

                recyclerView.setPadding(
                        insets.left,
                        insets.top,
                        insets.right,
                        isPortrait ? insets.bottom + 150 : insets.bottom
                );

                return WindowInsetsCompat.CONSUMED;
            });
        }


        // Temporary using the drawer container, it's the only way to set the content description at the moment.
        // The issue is posted to the FluentUI library: https://github.com/microsoft/fluentui-android/issues/758
        drawer.setContentDescription(getContext().getString(contentDescription));
    }
}
