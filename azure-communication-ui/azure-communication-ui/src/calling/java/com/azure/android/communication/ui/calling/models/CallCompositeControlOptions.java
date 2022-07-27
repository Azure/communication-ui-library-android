// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.graphics.drawable.Drawable;

public final class CallCompositeControlOptions {

    private CallCompositeControlOrderOptions controlOrderOptions = null;
    private Drawable micControlDrawable = null;
    private Drawable cameraControlDrawable = null;
    private Drawable audioControlDrawable = null;
    private Drawable hangupControlDrawable = null;

    public CallCompositeControlOrderOptions getControlOrderOptions() {
        return controlOrderOptions;
    }

    public void setControlOrderOptions(final CallCompositeControlOrderOptions controlOrderOptions) {
        this.controlOrderOptions = controlOrderOptions;
    }

    public Drawable getMicControlDrawable() {
        return micControlDrawable;
    }

    public void setMicControlDrawable(final Drawable micControlDrawable) {
        this.micControlDrawable = micControlDrawable;
    }

    public Drawable getCameraControlDrawable() {
        return cameraControlDrawable;
    }

    public void setCameraControlDrawable(final Drawable cameraControlDrawable) {
        this.cameraControlDrawable = cameraControlDrawable;
    }

    public Drawable getAudioControlDrawable() {
        return audioControlDrawable;
    }

    public void setAudioControlDrawable(final Drawable audioControlDrawable) {
        this.audioControlDrawable = audioControlDrawable;
    }

    public Drawable getHangupControlDrawable() {
        return hangupControlDrawable;
    }

    public void setHangupControlDrawable(final Drawable hangupControlDrawable) {
        this.hangupControlDrawable = hangupControlDrawable;
    }
}
