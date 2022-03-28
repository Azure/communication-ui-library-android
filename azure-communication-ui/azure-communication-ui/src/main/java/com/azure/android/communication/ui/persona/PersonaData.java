// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class PersonaData {
    private Bitmap image;
    private String name;

    public PersonaData(final Bitmap image) {
        this.image = image;
    }

    public PersonaData(final String name) {
        this.name = name;
    }

    public PersonaData(final String name, final Bitmap image) {
        this.image = image;
        this.name = name;
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
