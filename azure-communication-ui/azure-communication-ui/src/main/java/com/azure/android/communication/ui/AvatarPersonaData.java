// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import android.graphics.Bitmap;

import org.jetbrains.annotations.Nullable;

public class AvatarPersonaData {
    @Nullable
    private Bitmap avatarImageBitmap = null;

    public Bitmap getAvatarImageBitmap() {
        return avatarImageBitmap;
    }

    public void setAvatarImageBitmap(final Bitmap avatarImageBitmap) {
        this.avatarImageBitmap = avatarImageBitmap;
    }
}
