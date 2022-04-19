// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.configuration.CommunicationUILocalDataOptions;

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
 * &#47;&#47; Build the CommunicationUILocalDataOptions with CommunicationUIPersonaData
 * CommunicationUILocalDataOptions options =
 * new CommunicationUILocalDataOptions(new CommunicationUIPersonaData&#40;...&#41);
 *
 * </pre>
 *
 * @see CommunicationUILocalDataOptions
 */
public class CommunicationUIPersonaData {
    private Bitmap avatarBitmap;
    private String renderedDisplayName;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Create CommunicationUIPersonaData.
     *
     * @param avatarBitmap The {@link Bitmap};
     * @see CommunicationUILocalDataOptions
     */
    public CommunicationUIPersonaData(final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }

    /**
     * Create CommunicationUIPersonaData.
     *
     * @param avatarBitmap     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see CommunicationUILocalDataOptions
     */
    public CommunicationUIPersonaData(final Bitmap avatarBitmap, final ImageView.ScaleType scaleType) {
        this.avatarBitmap = avatarBitmap;
        this.scaleType = scaleType;
    }

    /**
     * Create CommunicationUIPersonaData.
     *
     * @param renderedDisplayName The {@link String};
     * @see CommunicationUILocalDataOptions
     */
    public CommunicationUIPersonaData(final String renderedDisplayName) {
        this.renderedDisplayName = renderedDisplayName;
    }

    /**
     * Create CommunicationUIPersonaData.
     *
     * @param renderedDisplayName  The {@link String};
     * @param avatarBitmap The {@link Bitmap};
     * @see CommunicationUILocalDataOptions
     */
    public CommunicationUIPersonaData(final String renderedDisplayName, final Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
        this.renderedDisplayName = renderedDisplayName;
    }

    /**
     * Create CommunicationUIPersonaData.
     *
     * @param renderedDisplayName      The {@link String};
     * @param avatarBitmap     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see CommunicationUILocalDataOptions
     */
    public CommunicationUIPersonaData(final String renderedDisplayName,
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
    @Nullable
    public String getRenderedDisplayName() {
        return renderedDisplayName;
    }

    /**
     * Get current avatar Bitmap
     *
     * @return The {@link Bitmap};
     */
    @Nullable
    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }
}
