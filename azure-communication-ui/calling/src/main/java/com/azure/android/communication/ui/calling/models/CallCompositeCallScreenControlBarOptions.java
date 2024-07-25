// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    final List<CallCompositeCustomButtonOptions> customButtons = new ArrayList<>();
    private CallCompositeButtonOptions cameraButtonOptions;
    private CallCompositeButtonOptions micOptions;
    private CallCompositeButtonOptions audioDeviceOptions;
    private CallCompositeButtonOptions liveCaptionsButtonOptions;
    private CallCompositeButtonOptions liveCaptionsToggleButtonOptions;
    private CallCompositeButtonOptions spokenLanguageButtonOptions;
    private CallCompositeButtonOptions captionsLanguageButtonOptions;
    private CallCompositeButtonOptions shareDiagnosticsButtonOptions;
    private CallCompositeButtonOptions reportIssueButtonOptions;

    private CallCompositeLeaveCallConfirmationMode leaveCallConfirmation =
            CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED;

    /**
     * Create a CallCompositeCallScreenControlBarOptions object.
     */
    public CallCompositeCallScreenControlBarOptions() {
    }

    /**
     * Set leave call confirmation.
     *
     * @param leaveCallConfirmation The leave call confirmation.
     * @return The {@link CallCompositeCallScreenControlBarOptions} object itself.
     */
    public CallCompositeCallScreenControlBarOptions setLeaveCallConfirmation(
            final CallCompositeLeaveCallConfirmationMode leaveCallConfirmation) {
        this.leaveCallConfirmation = leaveCallConfirmation;
        return this;
    }

    /**
     * Get leave call confirmation.
     *
     * @return {@link CallCompositeLeaveCallConfirmationMode} The leave call confirmation.
     */
    public CallCompositeLeaveCallConfirmationMode getLeaveCallConfirmation() {
        return leaveCallConfirmation;
    }

    /**
     * Add a custom button to the call composite.
     * @param buttonOptions {@link CallCompositeCallScreenControlBarOptions}
     */
    public CallCompositeCallScreenControlBarOptions addCustomButton(
            final CallCompositeCustomButtonOptions buttonOptions) {
        customButtons.add(buttonOptions);
        return this;
    }

    /**
     * Set customization to the camera button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setCameraButton(final CallCompositeButtonOptions buttonOptions) {
        this.cameraButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the camera button.
     */
    public CallCompositeButtonOptions getCameraButton() {
        return this.cameraButtonOptions;
    }

    /**
     * Set customization to the microphone button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setMicrophoneButton(
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
    public CallCompositeCallScreenControlBarOptions setAudioDeviceButton(
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

    /**
     * Set customization to the live captions button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setLiveCaptionsButton(
            final CallCompositeButtonOptions buttonOptions) {
        liveCaptionsButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live captions button.
     */
    public CallCompositeButtonOptions getLiveCaptionsButton() {
        return liveCaptionsButtonOptions;
    }

    /**
     * Set customization to the live captions toggle button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setLiveCaptionsToggleButton(
            final CallCompositeButtonOptions buttonOptions) {
        liveCaptionsToggleButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live captions toggle button.
     */
    public CallCompositeButtonOptions getLiveCaptionsToggleButton() {
        return liveCaptionsToggleButtonOptions;
    }

    /**
     * Set customization to the live cations spoken language button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setSpokenLanguageButton(
            final CallCompositeButtonOptions buttonOptions) {
        this.spokenLanguageButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live cations spoken language button.
     */
    public CallCompositeButtonOptions getSpokenLanguageButton() {
        return spokenLanguageButtonOptions;
    }

    /**
     * Set customization to the live cations language button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setCaptionsLanguageButton(
            final CallCompositeButtonOptions buttonOptions) {
        captionsLanguageButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live cations language button.
     */
    public CallCompositeButtonOptions getCaptionsLanguageButton() {
        return captionsLanguageButtonOptions;
    }

    /**
     * Set customization to the share diagnostics button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setShareDiagnosticsButton(
            final CallCompositeButtonOptions buttonOptions) {
        shareDiagnosticsButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization to the share diagnostics button.
     */
    public CallCompositeButtonOptions getShareDiagnosticsButton() {
        return shareDiagnosticsButtonOptions;
    }

    /**
     * Set customization to the report issue button.
     * @param buttonOptions {@link CallCompositeButtonOptions}
     */
    public CallCompositeCallScreenControlBarOptions setReportIssueButton(
            final CallCompositeButtonOptions buttonOptions) {
        reportIssueButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the report issue button.
     */
    public CallCompositeButtonOptions getReportIssueButton() {
        return reportIssueButtonOptions;
    }
}
