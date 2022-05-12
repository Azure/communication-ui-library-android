// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * ParticipantViewData for participant.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalSettings with ParticipantViewData
 * LocalSettings localSettings = new LocalSettings(new ParticipantViewData&#40;...&#41);
 *
 * callComposite.launch(..., ..., localSettings);
 *
 * </pre>
 *
 * @see LocalSettings
 */
public final class ParticipantViewData {
    private Bitmap avatarBitmap;
    private String renderedDisplayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Create ParticipantViewData.
     *
     * @see LocalSettings
     */
    public ParticipantViewData() {
    }

    /**
     * Create ParticipantViewData.
     *
     * @param avatarBitmap The {@link Bitmap};
     * @see LocalSettings
     */
    public ParticipantViewData(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }

    /**
     * Create ParticipantViewData.
     *
     * @param avatarBitmap     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see LocalSettings
     */
    public ParticipantViewData(final Bitmap avatarBitmap, final ImageView.ScaleType scaleType) {
        this.avatarBitmap = avatarBitmap;
        this.scaleType = scaleType;
    }

    /**
     * Create ParticipantViewData.
     *
     * @param renderedDisplayName The {@link String};
     * @see LocalSettings
     */
    public ParticipantViewData(final String renderedDisplayName) {
        this.renderedDisplayName = renderedDisplayName;
    }

    /**
     * Create ParticipantViewData.
     *
     * @param renderedDisplayName  The {@link String};
     * @param avatarBitmap The {@link Bitmap};
     * @see LocalSettings
     */
    public ParticipantViewData(final String renderedDisplayName, final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        this.renderedDisplayName = renderedDisplayName;
    }

    /**
     * Create ParticipantViewData.
     *
     * @param renderedDisplayName      The {@link String};
     * @param avatarBitmap     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see LocalSettings
     */
    public ParticipantViewData(final String renderedDisplayName,
                           final Bitmap avatarBitmap,
                           final ImageView.ScaleType scaleType) {
        this.renderedDisplayName = renderedDisplayName;
        this.avatarBitmap = avatarBitmap;
        this.scaleType = scaleType;
    }

    /**
     * Set scaleType
     *
     * @return The {@link ParticipantViewData};
     */
    public ParticipantViewData setScaleType(final ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    /**
     * Get scaleType
     *
     * @return The {@link ImageView.ScaleType};
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * Set rendered display name
     *
     * @return The {@link ParticipantViewData};
     */
    public ParticipantViewData setRenderedDisplayName(final String renderedDisplayName) {
        this.renderedDisplayName = renderedDisplayName;
        return this;
    }

    /**
     * Get rendered display name
     *
     * @return The {@link String};
     */
    public String getRenderedDisplayName() {
        return renderedDisplayName;
    }

    /**
     * Get avatar Bitmap
     *
     * @return The {@link Bitmap};
     */
    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }

    /**
     * Set avatar Bitmap
     *
     * @return The {@link ParticipantViewData};
     */
    public ParticipantViewData setAvatarBitmap(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        return this;
    }

}
