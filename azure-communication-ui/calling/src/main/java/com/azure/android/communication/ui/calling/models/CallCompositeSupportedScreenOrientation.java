// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.pm.ActivityInfo;

public enum CallCompositeSupportedScreenOrientation {
    PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
    LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
    REVERSE_LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE),
    FULL_SENSOR(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR),
    USER(ActivityInfo.SCREEN_ORIENTATION_USER);

    private final Integer screenOrientation;

    CallCompositeSupportedScreenOrientation(final int screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public int getSupportedScreenOrientation() {
        return this.screenOrientation;
    }
}
