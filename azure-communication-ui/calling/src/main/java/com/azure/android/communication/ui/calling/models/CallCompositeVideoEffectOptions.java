// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import androidx.annotation.NonNull;

import com.azure.android.communication.calling.VideoEffect;

public class CallCompositeVideoEffectOptions {
    private boolean videoEffectOn = false;
    private VideoEffect videoEffect = null;

    public CallCompositeVideoEffectOptions setVideoEffectOn(final boolean videoEffectOn) {
        this.videoEffectOn = videoEffectOn;
        return this;
    }

    public boolean isVideoEffectOn() {
        return this.videoEffectOn;
    }

    public CallCompositeVideoEffectOptions setVideoEffect(
            @NonNull final VideoEffect videoEffect) {
        this.videoEffect = videoEffect;
        return this;
    }

    public VideoEffect getVideoEffect() {
        return this.videoEffect;
    }
}

