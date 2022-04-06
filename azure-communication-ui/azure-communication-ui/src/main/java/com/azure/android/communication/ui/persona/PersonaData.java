// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration;

/**
 * PersonaData for local participant configuration.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalParticipantConfiguration with PersonaData
 * LocalParticipantConfiguration config = new LocalParticipantConfiguration(new PersonaData&#40;...&#41);
 *
 * </pre>
 *
 * @see LocalParticipantConfiguration
 */
public class PersonaData {
    private Bitmap image;
    private String name;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    /**
     * Create PersonaData.
     *
     * @param image The {@link Bitmap};
     * @see LocalParticipantConfiguration
     */
    public PersonaData(final Bitmap image) {
        this.image = image;
    }

    /**
     * Create PersonaData.
     *
     * @param image     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see LocalParticipantConfiguration
     */
    public PersonaData(final Bitmap image, final ImageView.ScaleType scaleType) {
        this.image = image;
        this.scaleType = scaleType;
    }

    /**
     * Create PersonaData.
     *
     * @param name The {@link String};
     * @see LocalParticipantConfiguration
     */
    public PersonaData(final String name) {
        this.name = name;
    }

    /**
     * Create PersonaData.
     *
     * @param name  The {@link String};
     * @param image The {@link Bitmap};
     * @see LocalParticipantConfiguration
     */
    public PersonaData(final String name, final Bitmap image) {
        this.image = image;
        this.name = name;
    }

    /**
     * Create PersonaData.
     *
     * @param name      The {@link String};
     * @param image     The {@link Bitmap};
     * @param scaleType The {@link ImageView.ScaleType};
     * @see LocalParticipantConfiguration
     */
    public PersonaData(final String name,
                       final Bitmap image,
                       final ImageView.ScaleType scaleType) {
        this.name = name;
        this.image = image;
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
     * Get current name
     *
     * @return The {@link String};
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Get current Bitmap
     *
     * @return The {@link Bitmap};
     */
    @Nullable
    public Bitmap getImage() {
        return image;
    }
}
