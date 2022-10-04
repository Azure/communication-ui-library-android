// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.azure.android.communication.ui.callwithchat.CallWithChatComposite;

public final class CallWithChatCompositeParticipantViewData {
    private Bitmap avatarBitmap;
    private String displayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Set scaleType.
     *
     * Will not take affect if called after
     * {@link CallWithChatCompositeParticipantViewData} passed to {@link CallWithChatComposite}
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeParticipantViewData setScaleType(final ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    /**
     * Get scaleType.
     *
     * Will not take affect if called after
     * {@link CallWithChatCompositeParticipantViewData} passed to {@link CallWithChatComposite}
     *
     * @return The {@link ImageView.ScaleType};
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * Set display name.
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeParticipantViewData setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Get display name.
     *
     * @return The {@link String};
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get avatar Bitmap.
     *
     * @return The {@link Bitmap};
     */
    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }

    /**
     * Set avatar Bitmap.
     *
     * Will not take affect if called after
     * {@link CallWithChatCompositeParticipantViewData} passed to {@link CallWithChatComposite}
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeParticipantViewData setAvatarBitmap(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        return this;
    }
}
