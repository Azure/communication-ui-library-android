// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ChatCompositeParticipantViewData {
    private Bitmap avatarBitmap;
    private String displayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public ChatCompositeParticipantViewData setScaleType(final ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatCompositeParticipantViewData setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }

    public ChatCompositeParticipantViewData setAvatarBitmap(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        return this;
    }
}
