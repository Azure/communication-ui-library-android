// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * {@link CallCompositeLocalOptions} for {@link CallComposite#launch}.
 *
 * <p>
 *     Local Options for the Call Composite. These options are not shared with the server and impact local views only.
 *     E.g. The Local Participant Name if it differs from the display name you'd like to share with the server.
 * </p>
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the CallCompositeLocalOptions with {@link CallCompositeParticipantViewData}
 * CallCompositeLocalOptions localOptions = new CallCompositeLocalOptions(
 *     new CallCompositeParticipantViewData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40;.., .., localOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class CallCompositeLocalOptions {
    private CallCompositeParticipantViewData participantViewData = null;
    private CallCompositeSetupScreenViewData setupScreenViewData = null;
    private boolean cameraOn = false;
    private boolean microphoneOn = false;
    private boolean skipSetupScreen = false;
    private CallCompositeAudioVideoMode audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO;
    private CallCompositeCaptionsOptions captionsOptions = null;

    private CallCompositeSetupScreenOptions setupScreenOptions = null;
    private CallCompositeCallScreenOptions callScreenOptions = null;
    /* <END_CALL_FOR_ALL>
    private boolean onCallEndTerminateForAll = false;
    </END_CALL_FOR_ALL> */

    /**
     * Create LocalSettings.
     *
     * @param participantViewData The {@link CallCompositeParticipantViewData};
     * @see CallCompositeParticipantViewData
     */
    public CallCompositeLocalOptions(final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Create an empty {@link CallCompositeLocalOptions} object and assign using setters.
     */
    public CallCompositeLocalOptions() { }

    /**
     * Get the {@link CallCompositeParticipantViewData}.
     *
     * @return The {@link CallCompositeParticipantViewData} that is currently set.
     */
    public CallCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }

    /**
     * Set a {@link CallCompositeParticipantViewData} to be used.
     * @param participantViewData The participant view data object to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setParticipantViewData(
            final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
        return this;
    }

    /**
     * Get the {@link CallCompositeSetupScreenViewData}.
     * @return The {@link CallCompositeSetupScreenViewData} that is currently set.
     */
    public CallCompositeSetupScreenViewData getSetupScreenViewData() {
        return setupScreenViewData;
    }

    /**
     * Set a {@link CallCompositeSetupScreenViewData} to be used.
     * @param setupScreenViewData The setup screen view data object to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setSetupScreenViewData(
            final CallCompositeSetupScreenViewData setupScreenViewData) {
        this.setupScreenViewData = setupScreenViewData;
        return this;
    }

    /**
     * Get the boolean value for skip setup screen.
     * @return The boolean that is currently set.
     */
    public boolean isSkipSetupScreen() {
        return this.skipSetupScreen;
    }

    /**
     * Set a boolean to be used.
     * @param skipSetupScreen The boolean value to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setSkipSetupScreen(final boolean skipSetupScreen) {
        this.skipSetupScreen = skipSetupScreen;
        return this;
    }

    /**
     * Get the initial camera configuration boolean value.
     * Note: If AUDIO_ONLY mode is set, this will always return false.
     * @return The boolean that is currently set.
     */
    public boolean isCameraOn() {
        //Override if the AV Mode is audio only
        if (audioVideoMode == CallCompositeAudioVideoMode.AUDIO_ONLY) {
            return false;
        }
        return this.cameraOn;
    }

    /**
     * Enables the Local Camera by default.
     * Note: If AvMode is set to Audio Only, this will have no effect
     * @param cameraOn The boolean value to be used for initial camera configuration.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setCameraOn(
            final boolean cameraOn
    ) {
        this.cameraOn = cameraOn;
        return this;
    }

    /**
     * Get the initial microphone configuration boolean value.
     * @return The boolean that is currently set.
     */
    public boolean isMicrophoneOn() {
        return this.microphoneOn;
    }

    /**
     * Set a boolean to be used.
     * @param microphoneOn The boolean value to be used for initial microphone configuration.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setMicrophoneOn(
            final boolean microphoneOn
    ) {
        this.microphoneOn = microphoneOn;
        return this;
    }


    /**
     * Sets the Audio/Video Mode of the local call.
     * Currently supported (Audio Only, Audio and Video)
     * Audio Only: This will disable the camera and incoming video feeds.
     * Audio and Video: This will enable the camera and incoming video feeds.
     * See {@link CallCompositeAudioVideoMode}
     * @param audioVideoMode The {@link CallCompositeAudioVideoMode} to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setAudioVideoMode(final CallCompositeAudioVideoMode audioVideoMode) {
        this.audioVideoMode = audioVideoMode;
        return this;
    }

    /**
     * Returns the Audio/Video mode of the local call
     *
     * @return The boolean value to be used for audio only mode.
     */
    public CallCompositeAudioVideoMode getAudioVideoMode() {
        return audioVideoMode;
    }

    /**
     * Set setup screen options.
     * @param setupScreenOptions {@link CallCompositeSetupScreenOptions}
     */
    public CallCompositeLocalOptions setSetupScreenOptions(final CallCompositeSetupScreenOptions setupScreenOptions) {
        this.setupScreenOptions = setupScreenOptions;
        return this;
    }

    /**
     * Get setup screen options.
     */
    public CallCompositeSetupScreenOptions getSetupScreenOptions() {
        return setupScreenOptions;
    }

    /**
     * Set call screen options.
     * @param callScreenOptions {@link CallCompositeCallScreenOptions}
     */
    public CallCompositeLocalOptions setCallScreenOptions(final CallCompositeCallScreenOptions callScreenOptions) {
        this.callScreenOptions = callScreenOptions;
        return this;
    }

    /**
     * Get call screen options.
     */
    public CallCompositeCallScreenOptions getCallScreenOptions() {
        return callScreenOptions;
    }

    /**
     * Get the {@link CallCompositeCaptionsOptions}.
     * @return The {@link CallCompositeCaptionsOptions} that is currently set.
     */
    public CallCompositeCaptionsOptions getCaptionsOptions() {
        return captionsOptions;
    }

    /**
     * Set a {@link CallCompositeCaptionsOptions} to be used.
     * @param captionsOptions The captions options object to be used.
     * @return The current {@link CallCompositeLocalOptions}.
     */
    public CallCompositeLocalOptions setCaptionsOptions(
            final CallCompositeCaptionsOptions captionsOptions) {
        this.captionsOptions = captionsOptions;
        return this;
    }

    /* <END_CALL_FOR_ALL>
    \**
     * Get the boolean value for on call end terminate for all.
     * @return The boolean that is currently set.
     *\
    public boolean isOnCallEndTerminateForAll() {
        return this.onCallEndTerminateForAll;
    }

    \**
     * Set a boolean to be used.
     * @param onCallEndTerminateForAll The boolean value to be used for on call end terminate for all.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     *\
    public CallCompositeLocalOptions setOnCallEndTerminateForAll(final boolean onCallEndTerminateForAll) {
        this.onCallEndTerminateForAll = onCallEndTerminateForAll;
        return this;
    }
    </END_CALL_FOR_ALL> */
}
