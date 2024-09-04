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

    private CallCompositeButtonViewData cameraButtonOptions;
    private CallCompositeButtonViewData micOptions;
    private CallCompositeButtonViewData audioDeviceOptions;

    /**
     * Creates {@link CallCompositeSetupScreenOptions}.
     */
    public CallCompositeSetupScreenOptions() {
    }

    /**
     * @deprecated Use {@link #setCameraButton(CallCompositeButtonViewData)} instead.
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
     * @deprecated Use {@link #setCameraButton(CallCompositeButtonViewData)} instead.
     * Is camera button enabled to user.
     */
    @Deprecated
    public Boolean isCameraButtonEnabled() {
        return this.cameraButtonEnabled;
    }

    /**
     * @deprecated Use {@link #setMicrophoneButton(CallCompositeButtonViewData)} instead.
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
     * @deprecated Use {@link #setMicrophoneButton(CallCompositeButtonViewData)} instead.
     * Is microphone button enabled to user.
     */
    @Deprecated
    public Boolean isMicrophoneButtonEnabled() {
        return this.micButtonEnabled;
    }

    /**
     * Set customization to the camera button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeSetupScreenOptions setCameraButton(final CallCompositeButtonViewData buttonOptions) {
        this.cameraButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization to the camera button.
     */
    public CallCompositeButtonViewData getCameraButton() {
        return this.cameraButtonOptions;
    }

    /**
     * Set customization to the microphone button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeSetupScreenOptions setMicrophoneButton(
            final CallCompositeButtonViewData buttonOptions) {
        micOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the microphone button.
     */
    public CallCompositeButtonViewData getMicrophoneButton() {
        return micOptions;
    }

    /**
     * Set customization to the audio device button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeSetupScreenOptions setAudioDeviceButton(
            final CallCompositeButtonViewData buttonOptions) {
        audioDeviceOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the audio device button.
     */
    public CallCompositeButtonViewData getAudioDeviceButton() {
        return audioDeviceOptions;
    }
}
