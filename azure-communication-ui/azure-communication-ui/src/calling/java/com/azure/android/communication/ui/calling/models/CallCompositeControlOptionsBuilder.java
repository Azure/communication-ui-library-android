// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public final class CallCompositeControlOptionsBuilder {

    private CallCompositeControlOrderOptions controlOrderOptions = null;
    private Drawable micControlDrawable = null;
    private Drawable cameraControlDrawable = null;
    private Drawable audioControlDrawable = null;
    private Drawable hangupControlDrawable = null;

    public CallCompositeControlOptionsBuilder controlOrderOptions(
            @NonNull final CallCompositeControlOrderOptions controlOrderOptions) {
        this.controlOrderOptions = controlOrderOptions;
        return this;
    }

    public CallCompositeControlOptionsBuilder setMicControlDrawable(
            @NonNull final Drawable micControlDrawable) {
        this.micControlDrawable = micControlDrawable;
        return this;
    }

    public CallCompositeControlOptionsBuilder setCameraControlDrawable(@NonNull final Drawable cameraControlDrawable) {
        this.cameraControlDrawable = cameraControlDrawable;
        return this;
    }

    public CallCompositeControlOptionsBuilder setAudioControlDrawable(@NonNull final Drawable audioControlDrawable) {
        this.audioControlDrawable = audioControlDrawable;
        return this;
    }

    public CallCompositeControlOptionsBuilder setHangupControlDrawable(@NonNull final Drawable hangupControlDrawable) {
        this.hangupControlDrawable = hangupControlDrawable;
        return this;
    }

    public CallCompositeControlOptions build() {
        final CallCompositeControlOptions controlOptions = new CallCompositeControlOptions();

        if (this.controlOrderOptions != null) {
            controlOptions.setControlOrderOptions(this.controlOrderOptions);
        } else {
            controlOptions.setControlOrderOptions(new CallCompositeControlOrderOptions());
        }

        if (this.micControlDrawable != null) {
            controlOptions.setMicControlDrawable(this.micControlDrawable);
        }
        if (this.cameraControlDrawable != null) {
            controlOptions.setCameraControlDrawable(this.cameraControlDrawable);
        }
        if (this.audioControlDrawable != null) {
            controlOptions.setAudioControlDrawable(this.audioControlDrawable);
        }
        if (this.hangupControlDrawable != null) {
            controlOptions.setHangupControlDrawable(this.hangupControlDrawable);
        }

        return controlOptions;
    }
}
