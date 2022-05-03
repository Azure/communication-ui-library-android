// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.persona;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.azure.android.communication.ui.calling.configuration.LocalDataOptions;

/**
 * PersonaData for local participant.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalDataOptions with PersonaData
 * LocalDataOptions options =
 * new LocalDataOptions(new PersonaData&#40;...&#41);
 *
 * </pre>
 *
 * @see LocalDataOptions
 */
public final class PersonaData {
    private Bitmap avatarBitmap;
    private String renderedDisplayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Create PersonaData.
     *
     * @param avatarBitmap The {@link Bitmap};
     * @see LocalDataOptions
     */
    public PersonaData(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }

    /**
     * Create PersonaData.
     *
     * @param avatarBitmap     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see LocalDataOptions
     */
    public PersonaData(final Bitmap avatarBitmap, final ImageView.ScaleType scaleType) {
        this.avatarBitmap = avatarBitmap;
        this.scaleType = scaleType;
    }

    /**
     * Create PersonaData.
     *
     * @param renderedDisplayName The {@link String};
     * @see LocalDataOptions
     */
    public PersonaData(final String renderedDisplayName) {
        this.renderedDisplayName = renderedDisplayName;
    }

    /**
     * Create PersonaData.
     *
     * @param renderedDisplayName  The {@link String};
     * @param avatarBitmap The {@link Bitmap};
     * @see LocalDataOptions
     */
    public PersonaData(final String renderedDisplayName, final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        this.renderedDisplayName = renderedDisplayName;
    }

    /**
     * Create PersonaData.
     *
     * @param renderedDisplayName      The {@link String};
     * @param avatarBitmap     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see LocalDataOptions
     */
    public PersonaData(final String renderedDisplayName,
                       final Bitmap avatarBitmap,
                       final ImageView.ScaleType scaleType) {
        this.renderedDisplayName = renderedDisplayName;
        this.avatarBitmap = avatarBitmap;
        this.scaleType = scaleType;
    }

    /**
     * Get current scaleType
     *
     * @return The {@link ImageView.ScaleType};
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
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
     * Get current avatar Bitmap
     *
     * @return The {@link Bitmap};
     */
    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }
}
