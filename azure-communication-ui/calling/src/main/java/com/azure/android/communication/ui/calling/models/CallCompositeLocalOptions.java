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
    private Boolean cameraOnByDefault = false;
    private Boolean microphoneOnByDefault = false;
    private Boolean bypassSetupScreen = false;
    private CallCompositeParticipantRole roleHint = null;

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
     * Get the {@link Boolean}.
     * @return The {@link Boolean} that is currently set.
     */
    public Boolean getBypassSetupScreen() {
        return this.bypassSetupScreen;
    }

    /**
     * Set a boolean to be used.
     * @param bypassSetupScreen The boolean value to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setBypassSetupScreen(final boolean bypassSetupScreen) {
        this.bypassSetupScreen = bypassSetupScreen;
        return this;
    }

    /**
     * Get the {@link Boolean}.
     * @return The {@link Boolean} that is currently set.
     */
    public Boolean getCameraOnByDefault() {
        return this.cameraOnByDefault;
    }

    /**
     * Set a boolean to be used.
     * @param cameraOnByDefault The boolean value to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setCameraOnByDefault(
            final boolean cameraOnByDefault
    ) {
        this.cameraOnByDefault = cameraOnByDefault;
        return this;
    }

    /**
     * Get the {@link Boolean}.
     * @return The {@link Boolean} that is currently set.
     */
    public Boolean getMicrophoneOnByDefault() {
        return this.microphoneOnByDefault;
    }

    /**
     * Set a boolean to be used.
     * @param microphoneOnByDefault The boolean value to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setMicrophoneOnByDefault(
            final boolean microphoneOnByDefault
    ) {
        this.microphoneOnByDefault = microphoneOnByDefault;
        return this;
    }

    /**
     * Get role hint.
     * @return {@link CallCompositeParticipantRole}
     */
    public CallCompositeParticipantRole getRoleHint() {
        return roleHint;
    }

    /**
     * Get role hint. Use this to hint the role of the user when the role is not available before a Rooms
     * call is started.
     * This value should be obtained using the Rooms API. This role will determine permissions in the
     * Setup screen of the {@link CallComposite}.
     * The true role of the user will be synced with ACS services when a Rooms call starts.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setRoleHint(final CallCompositeParticipantRole roleHint) {
        this.roleHint = roleHint;
        return this;
    }
}
