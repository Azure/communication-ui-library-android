// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class PersonaData {
    private Bitmap image;
    private String name;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    public PersonaData(final Bitmap image) {
        this.image = image;
    }

    public PersonaData(final Bitmap image, final ImageView.ScaleType scaleType) {
        this.image = image;
        this.scaleType = scaleType;
    }


    public PersonaData(final String name) {
        this.name = name;
    }

    public PersonaData(final String name, final Bitmap image) {
        this.image = image;
        this.name = name;
    }


    public PersonaData(final String name, final Bitmap image,
                       final ImageView.ScaleType scaleType) {
        this.name = name;
        this.image = image;
        this.scaleType = scaleType;
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Bitmap getImage() {
        return image;
    }
}
