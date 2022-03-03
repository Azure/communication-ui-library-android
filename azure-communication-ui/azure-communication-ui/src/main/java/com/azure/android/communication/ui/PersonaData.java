// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import android.graphics.Bitmap;

import org.jetbrains.annotations.Nullable;

/**
 * Get and set values for Fluent UI Avatar View
 */
public class PersonaData {

    @Nullable
    private Bitmap avatarImageBitmap = null;

    /**
     * @return bitmap avatar data
     */
    public Bitmap getAvatarImageBitmap() {
        return avatarImageBitmap;
    }

    /**
     * @param avatarImageBitmap
     */
    public void setAvatarImageBitmap(final Bitmap avatarImageBitmap) {
        this.avatarImageBitmap = avatarImageBitmap;
    }
}
