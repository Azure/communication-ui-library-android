// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * CallCompositeParticipantViewData for participant.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalSettings with {@link CallCompositeParticipantViewData}
 * CallCompositeClientOptions clientOptions = new CallCompositeClientOptions(
 *     new CallCompositeParticipantViewData&#40;...&#41);
 *
 * callComposite.launch(..., ..., clientOptions);
 *
 * </pre>
 *
 * @see CallCompositeClientOptions
 */
public final class CallCompositeParticipantViewData {
    private Bitmap avatarBitmap;
    private String displayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Set scaleType.
     *
     * @return The {@link CallCompositeParticipantViewData};
     */
    public CallCompositeParticipantViewData setScaleType(final ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    /**
     * Get scaleType.
     *
     * @return The {@link ImageView.ScaleType};
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * Set display name.
     *
     * @return The {@link CallCompositeParticipantViewData};
     */
    public CallCompositeParticipantViewData setDisplayName(final String displayName) {
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
     * @return The {@link CallCompositeParticipantViewData};
     */
    public CallCompositeParticipantViewData setAvatarBitmap(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        return this;
    }

}
