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

    private CallCompositeButtonOptions cameraButtonOptions;
    private CallCompositeButtonOptions micOptions;
    private CallCompositeButtonOptions audioDeviceOptions;

    /**
     * Creates {@link CallCompositeSetupScreenOptions}.
     */
    public CallCompositeSetupScreenOptions() {
    }

    /**
     * @deprecated Use {@link #setCameraButton(CallCompositeButtonOptions)} instead.
     * Set camera button enabled to user. Enabled by default.
     * @param enabled Sets camera button enable/disabled on the setup screen.
     * @return {@link CallCompositeSetupScreenOptions}.
     */
    @Deprecated
    public CallCompositeSetupScreenOptions setCameraButtonEnabled(final Boolean enabled) {
        this.cameraButtonEnabled = enabled;
        return this;
    }

    /**
     * @deprecated Use {@link #setCameraButton(CallCompositeButtonOptions)} instead.
     * Is camera button enabled to user.
     */
    @Deprecated
    public Boolean isCameraButtonEnabled() {
        return this.cameraButtonEnabled;
    }

    /**
     * @deprecated Use {@link #setMicrophoneButton(CallCompositeButtonOptions)} instead.
     * Set microphone button enabled to user. Enabled by default.
     * @param enabled Sets microphone button enable/disabled on the setup screen.
     * @return {@link CallCompositeSetupScreenOptions}.
     */
    @Deprecated
    public CallCompositeSetupScreenOptions setMicrophoneButtonEnabled(final Boolean enabled) {
        this.micButtonEnabled = enabled;
        return this;
    }

    /**
     * @deprecated Use {@link #setMicrophoneButton(CallCompositeButtonOptions)} instead.
     * Is microphone button enabled to user.
     */
    @Deprecated
    public Boolean isMicrophoneButtonEnabled() {
        return this.micButtonEnabled;
    }

    /**
     * Set customization to the camera button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeSetupScreenOptions setCameraButton(final CallCompositeButtonOptions buttonOptions) {
        this.cameraButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization to the camera button.
     */
    public CallCompositeButtonOptions getCameraButton() {
        return this.cameraButtonOptions;
    }

    /**
     * Set customization to the microphone button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeSetupScreenOptions setMicrophoneButton(
            final CallCompositeButtonOptions buttonOptions) {
        micOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the microphone button.
     */
    public CallCompositeButtonOptions getMicrophoneButton() {
        return micOptions;
    }

    /**
     * Set customization to the audio device button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeSetupScreenOptions setAudioDeviceButton(
            final CallCompositeButtonOptions buttonOptions) {
        audioDeviceOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the audio device button.
     */
    public CallCompositeButtonOptions getAudioDeviceButton() {
        return audioDeviceOptions;
    }
}
