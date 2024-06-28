// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Setup screen options to provide for {@link CallComposite}.
 */
public final class CallCompositeSetupScreenOptions {

    private Boolean cameraButtonEnabled = null;
    private Boolean micButtonEnabled = null;

    /**
     * Creates {@link CallCompositeSetupScreenOptions}.
     */
    public CallCompositeSetupScreenOptions() {
    }

    /**
     * Set camera button enabled to user. Enabled by default.
     * @param enabled Sets camera button enable/disabled on the setup screen.
     * @return {@link CallCompositeSetupScreenOptions}.
     */
    public CallCompositeSetupScreenOptions setCameraButtonEnabled(final Boolean enabled) {
        this.cameraButtonEnabled = enabled;
        return this;
    }

    /**
     * Is camera button enabled to user.
     */
    public Boolean isCameraButtonEnabled() {
        return this.cameraButtonEnabled;
    }

    /**
     * Set microphone button enabled to user. Enabled by default.
     * @param enabled Sets microphone button enable/disabled on the setup screen.
     * @return {@link CallCompositeSetupScreenOptions}.
     */
    public CallCompositeSetupScreenOptions setMicrophoneButtonEnabled(final Boolean enabled) {
        this.micButtonEnabled = enabled;
        return this;
    }

    /**
     * Is microphone button enabled to user.
     */
    public Boolean isMicrophoneButtonEnabled() {
        return this.micButtonEnabled;
    }
}
