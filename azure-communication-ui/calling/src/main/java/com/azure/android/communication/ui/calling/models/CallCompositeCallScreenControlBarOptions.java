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
    private CallCompositeButtonViewData cameraButton;
    private CallCompositeButtonViewData micButton;
    private CallCompositeButtonViewData audioDeviceButton;
    private CallCompositeButtonViewData liveCaptionsButton;
    private CallCompositeButtonViewData liveCaptionsToggleButton;
    private CallCompositeButtonViewData spokenLanguageButton;
    private CallCompositeButtonViewData captionsLanguageButton;
    private CallCompositeButtonViewData shareDiagnosticsButton;
    private CallCompositeButtonViewData reportIssueButton;

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
     * @param button {@link CallCompositeCallScreenControlBarOptions}
     */
    public CallCompositeCallScreenControlBarOptions setCustomButtons(
            final List<CallCompositeCustomButtonViewData> button) {
        customButtons = button;
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
     * @param button {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setCameraButton(final CallCompositeButtonViewData button) {
        this.cameraButton = button;
        return this;
    }

    /**
     * Get customization of the camera button.
     */
    public CallCompositeButtonViewData getCameraButton() {
        return this.cameraButton;
    }

    /**
     * Set customization to the microphone button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setMicrophoneButton(
            final CallCompositeButtonViewData buttonOptions) {
        micButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the microphone button.
     */
    public CallCompositeButtonViewData getMicrophoneButton() {
        return micButton;
    }

    /**
     * Set customization to the audio device button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setAudioDeviceButton(
            final CallCompositeButtonViewData buttonOptions) {
        audioDeviceButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the audio device button.
     */
    public CallCompositeButtonViewData getAudioDeviceButton() {
        return audioDeviceButton;
    }

    /**
     * Set customization to the live captions button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setLiveCaptionsButton(
            final CallCompositeButtonViewData buttonOptions) {
        liveCaptionsButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live captions button.
     */
    public CallCompositeButtonViewData getLiveCaptionsButton() {
        return liveCaptionsButton;
    }

    /**
     * Set customization to the live captions toggle button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setLiveCaptionsToggleButton(
            final CallCompositeButtonViewData buttonOptions) {
        liveCaptionsToggleButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live captions toggle button.
     */
    public CallCompositeButtonViewData getLiveCaptionsToggleButton() {
        return liveCaptionsToggleButton;
    }

    /**
     * Set customization to the live cations spoken language button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setSpokenLanguageButton(
            final CallCompositeButtonViewData buttonOptions) {
        this.spokenLanguageButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live cations spoken language button.
     */
    public CallCompositeButtonViewData getSpokenLanguageButton() {
        return spokenLanguageButton;
    }

    /**
     * Set customization to the live cations language button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setCaptionsLanguageButton(
            final CallCompositeButtonViewData buttonOptions) {
        captionsLanguageButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the live cations language button.
     */
    public CallCompositeButtonViewData getCaptionsLanguageButton() {
        return captionsLanguageButton;
    }

    /**
     * Set customization to the share diagnostics button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setShareDiagnosticsButton(
            final CallCompositeButtonViewData buttonOptions) {
        shareDiagnosticsButton = buttonOptions;
        return this;
    }

    /**
     * Get customization to the share diagnostics button.
     */
    public CallCompositeButtonViewData getShareDiagnosticsButton() {
        return shareDiagnosticsButton;
    }

    /**
     * Set customization to the report issue button.
     * @param buttonOptions {@link CallCompositeButtonViewData}
     */
    public CallCompositeCallScreenControlBarOptions setReportIssueButton(
            final CallCompositeButtonViewData buttonOptions) {
        reportIssueButton = buttonOptions;
        return this;
    }

    /**
     * Get customization of the report issue button.
     */
    public CallCompositeButtonViewData getReportIssueButton() {
        return reportIssueButton;
    }
}
