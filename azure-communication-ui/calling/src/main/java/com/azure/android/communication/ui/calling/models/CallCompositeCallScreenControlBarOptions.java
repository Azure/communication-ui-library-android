// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for the CallCompositeCallScreenControlBarOptions.
 */
public final class CallCompositeCallScreenControlBarOptions {
    private List<CallCompositeCustomButtonViewData> customButtons = new ArrayList<>();
    private CallCompositeButtonViewData cameraButtonOptions;
    private CallCompositeButtonViewData micOptions;
    private CallCompositeButtonViewData audioDeviceOptions;
    private CallCompositeButtonViewData liveCaptionsButtonOptions;
    private CallCompositeButtonViewData liveCaptionsToggleButtonOptions;
    private CallCompositeButtonViewData spokenLanguageButtonOptions;
    private CallCompositeButtonViewData captionsLanguageButtonOptions;
    private CallCompositeButtonViewData shareDiagnosticsButtonOptions;
    private CallCompositeButtonViewData reportIssueButtonOptions;

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
     * Set a custom button to the call composite.
     * @param buttonOptions {@link CallCompositeCallScreenControlBarOptions}
     */
    public CallCompositeCallScreenControlBarOptions setCustomButtons(
            final List<CallCompositeCustomButtonViewData> buttonOptions) {
        customButtons = buttonOptions;
        return this;
    }

    /**
     * Get a custom button to the call composite.
     */
    public List<CallCompositeCustomButtonViewData> getCustomButtons() {
        return customButtons;
    }

    /**
     * Set customization to the camera button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setCameraButton(final CallCompositeButtonViewData buttonOptions) {
        this.cameraButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the camera button.
     */
    public CallCompositeButtonViewData getCameraButton() {
        return this.cameraButtonOptions;
    }

    /**
     * Set customization to the microphone button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setMicrophoneButton(
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
    public CallCompositeCallScreenControlBarOptions setAudioDeviceButton(
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

    /**
     * Set customization to the live captions button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setLiveCaptionsButton(
            final CallCompositeButtonViewData buttonOptions) {
        liveCaptionsButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live captions button.
     */
    public CallCompositeButtonViewData getLiveCaptionsButton() {
        return liveCaptionsButtonOptions;
    }

    /**
     * Set customization to the live captions toggle button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setLiveCaptionsToggleButton(
            final CallCompositeButtonViewData buttonOptions) {
        liveCaptionsToggleButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live captions toggle button.
     */
    public CallCompositeButtonViewData getLiveCaptionsToggleButton() {
        return liveCaptionsToggleButtonOptions;
    }

    /**
     * Set customization to the live cations spoken language button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setSpokenLanguageButton(
            final CallCompositeButtonViewData buttonOptions) {
        this.spokenLanguageButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live cations spoken language button.
     */
    public CallCompositeButtonViewData getSpokenLanguageButton() {
        return spokenLanguageButtonOptions;
    }

    /**
     * Set customization to the live cations language button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setCaptionsLanguageButton(
            final CallCompositeButtonViewData buttonOptions) {
        captionsLanguageButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live cations language button.
     */
    public CallCompositeButtonViewData getCaptionsLanguageButton() {
        return captionsLanguageButtonOptions;
    }

    /**
     * Set customization to the share diagnostics button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setShareDiagnosticsButton(
            final CallCompositeButtonViewData buttonOptions) {
        shareDiagnosticsButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization to the share diagnostics button.
     */
    public CallCompositeButtonViewData getShareDiagnosticsButton() {
        return shareDiagnosticsButtonOptions;
    }

    /**
     * Set customization to the report issue button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setReportIssueButton(
            final CallCompositeButtonViewData buttonOptions) {
        reportIssueButtonOptions = buttonOptions;
        return this;
    }

    /**
     * Get customization of the report issue button.
     */
    public CallCompositeButtonViewData getReportIssueButton() {
        return reportIssueButtonOptions;
    }
}
