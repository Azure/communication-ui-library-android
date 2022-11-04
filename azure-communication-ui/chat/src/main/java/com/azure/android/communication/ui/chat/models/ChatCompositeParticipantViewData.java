// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.azure.android.communication.ui.chat.ChatManager;

/**
 * ChatCompositeParticipantViewData for participant.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the chat composite builder
 * final ChatCompositeBuilder builder = new ChatCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the chat composite
 * ChatComposite chatComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the ChatCompositeLocalOptions with {@link ChatCompositeParticipantViewData}
 * ChatCompositeLocalOptions localOptions = new ChatCompositeLocalOptions(
 *     new ChatCompositeParticipantViewData&#40;...&#41);
 *
 * chatComposite.launch(..., ..., localOptions);
 *
 * </pre>
 *
 * @see ChatCompositeLocalOptions
 */
public final class ChatCompositeParticipantViewData {
    private Bitmap avatarBitmap;
    private String displayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Get scaleType.
     * <p>
     * Will not take affect if called after {@link ChatCompositeParticipantViewData} passed to {@link ChatManager}
     *
     * @return The {@link ImageView.ScaleType};
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * Set scaleType.
     * <p>
     * Will not take affect if called after {@link ChatCompositeParticipantViewData} passed to {@link ChatManager}
     *
     * @return The {@link ChatCompositeParticipantViewData};
     */
    public ChatCompositeParticipantViewData setScaleType(final ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
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
     * Set display name.
     *
     * @return The {@link ChatCompositeParticipantViewData};
     */
    public ChatCompositeParticipantViewData setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
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
     * <p>
     * Will not take affect if called after {@link ChatCompositeParticipantViewData} passed to {@link ChatManager}
     *
     * @return The {@link ChatCompositeParticipantViewData};
     */
    public ChatCompositeParticipantViewData setAvatarBitmap(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        return this;
    }

}
