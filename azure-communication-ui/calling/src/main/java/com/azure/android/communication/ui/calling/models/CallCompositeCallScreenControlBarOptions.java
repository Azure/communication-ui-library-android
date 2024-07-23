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

    public CallCompositeCallScreenControlBarOptions addCustomButton(
            final CallCompositeCustomButtonOptions buttonOptions) {
        customButtons.add(buttonOptions);
        return this;
    }

    public CallCompositeCallScreenControlBarOptions setCameraButton(final CallCompositeButtonOptions buttonOptions) {
        this.cameraButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getCameraButton() {
        return this.cameraButtonOptions;
    }

    public CallCompositeCallScreenControlBarOptions setMicrophoneButton(
            final CallCompositeButtonOptions buttonOptions) {
        micOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getMicrophoneButton() {
        return micOptions;
    }

    public CallCompositeCallScreenControlBarOptions setAudioDeviceButton(
            final CallCompositeButtonOptions buttonOptions) {
        audioDeviceOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getAudioDeviceButton() {
        return audioDeviceOptions;
    }

    public CallCompositeCallScreenControlBarOptions setLiveCaptionsButton(
            final CallCompositeButtonOptions buttonOptions) {
        liveCaptionsButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getLiveCaptionsButton() {
        return liveCaptionsButtonOptions;
    }

    public CallCompositeCallScreenControlBarOptions setLiveCaptionsToggleButton(
            final CallCompositeButtonOptions buttonOptions) {
        liveCaptionsToggleButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getLiveCaptionsToggleButton() {
        return liveCaptionsToggleButtonOptions;
    }

    public CallCompositeCallScreenControlBarOptions setSpokenLanguageButton(
            final CallCompositeButtonOptions buttonOptions) {
        this.spokenLanguageButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getSpokenLanguageButton() {
        return spokenLanguageButtonOptions;
    }

    public CallCompositeCallScreenControlBarOptions setCaptionsLanguageButton(
            final CallCompositeButtonOptions buttonOptions) {
        captionsLanguageButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getCaptionsLanguageButton() {
        return captionsLanguageButtonOptions;
    }

    public CallCompositeCallScreenControlBarOptions setShareDiagnosticsButton(
            final CallCompositeButtonOptions buttonOptions) {
        shareDiagnosticsButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getShareDiagnosticsButton() {
        return shareDiagnosticsButtonOptions;
    }

    public CallCompositeCallScreenControlBarOptions setReportIssueButton(
            final CallCompositeButtonOptions buttonOptions) {
        reportIssueButtonOptions = buttonOptions;
        return this;
    }

    public CallCompositeButtonOptions getReportIssueButton() {
        return reportIssueButtonOptions;
    }

}
