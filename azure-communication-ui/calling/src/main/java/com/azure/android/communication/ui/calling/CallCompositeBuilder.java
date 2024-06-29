// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilitiesChangedNotificationMode;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import android.content.Context;
import com.azure.android.communication.common.CommunicationTokenCredential;
/*  <DEFAULT_AUDIO_MODE:0>
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionMode;
</DEFAULT_AUDIO_MODE:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation;
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerOptions;

/**
 * Builder for creating {@link CallComposite}.
 *
 * <p>Used to build a {@link CallComposite} which is then used to start a call.</p>
 * <p>This class can be used to specify a Custom theme or locale to be used by the Call Composite.</p>
 */
public final class CallCompositeBuilder {

    private Integer themeConfig = null;
    private CallCompositeLocalizationOptions localizationConfig = null;
    private Boolean enableMultitasking = false;
    private Boolean enableSystemPiPWhenMultitasking = false;
    private CallCompositeSupportedScreenOrientation callScreenOrientation = null;
    private CallCompositeSupportedScreenOrientation setupScreenOrientation = null;
    private CallCompositeCapabilitiesChangedNotificationMode capabilitiesChangedNotificationMode = null;
    private CallCompositeSetupScreenOptions setupScreenOptions = null;
    private CallCompositeCallScreenOptions callScreenOptions = null;
    private CallCompositeTelecomManagerOptions telecomManagerOptions = null;
    private Context applicationContext = null;
    private String displayName = null;
    private CommunicationTokenCredential credential = null;
    private Boolean disableInternalPushForIncomingCall = false;
    private CommunicationIdentifier identifier;
    /*  <DEFAULT_AUDIO_MODE:0>
    private CallCompositeAudioSelectionMode audioSelectionMode = null;
    </DEFAULT_AUDIO_MODE:0> */

    /**
     * Sets an optional theme for call-composite to use by {@link CallComposite}.
     *
     * @param themeId Theme ID.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder theme(final int themeId) {
        this.themeConfig = themeId;
        return this;
    }

    /**
     * Sets an optional localization for call-composite to use by {@link CallComposite}.
     *
     * @param localization {@link CallCompositeLocalizationOptions}.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder localization(final CallCompositeLocalizationOptions localization) {
        this.localizationConfig = localization;
        return this;
    }

    /***
     * While on the call, user can go back to previous activity from the call composite.
     *
     * @param options Multitasking options.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder multitasking(
            final CallCompositeMultitaskingOptions options) {
        this.enableMultitasking = options.isMultitaskingEnabled();
        this.enableSystemPiPWhenMultitasking = options.isSystemPictureInPictureEnabled();
        return this;
    }

    /***
     * Sets an optional orientation for call screen of the call-composite
     *
     * @param callScreenOrientation {@link CallCompositeSupportedScreenOrientation}
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder callScreenOrientation(
            final CallCompositeSupportedScreenOrientation callScreenOrientation) {
        this.callScreenOrientation = callScreenOrientation;
        return this;
    }

    /***
     * Sets an optional orientation for setup screen of the call-composite
     *
     * @param setupScreenOrientation {@link CallCompositeSupportedScreenOrientation}
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder setupScreenOrientation(
            final CallCompositeSupportedScreenOrientation setupScreenOrientation) {
        this.setupScreenOrientation = setupScreenOrientation;
        return this;
    }

    /**
     * Sets capabilities change notification mode.
     * @param mode see {@link CallCompositeLocalOptions}
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeBuilder capabilitiesChangedNotificationMode(
            final CallCompositeCapabilitiesChangedNotificationMode mode
    ) {
        this.capabilitiesChangedNotificationMode = mode;
        return this;
    }

    /**
     * Sets call screen options.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeBuilder setupScreenOptions(
            final CallCompositeSetupScreenOptions options) {
        this.setupScreenOptions = options;
        return this;
    }

    /**
     * Sets the call screen options.
     *
     * @param callScreenOptions call screen options.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder callScreenOptions(final CallCompositeCallScreenOptions callScreenOptions) {
        this.callScreenOptions = callScreenOptions;
        return this;
    }

    /**
     * Sets an optional telecom manager options for call-composite to use by {@link CallComposite}.
     *
     * @param telecomManagerOptions {@link CallCompositeTelecomManagerOptions}.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder telecomManagerOptions(
            final CallCompositeTelecomManagerOptions telecomManagerOptions) {
        this.telecomManagerOptions = telecomManagerOptions;
        return this;
    }

    /**
     * Sets the display name.
     *
     * @param displayName display name.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder displayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets the application context.
     *
     * @param applicationContext application context.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder applicationContext(final Context applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    /**
     * Sets the credential.
     *
     * @param credential {@link CommunicationTokenCredential}.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder credential(final CommunicationTokenCredential credential) {
        this.credential = credential;
        return this;
    }

    /**
     * Sets the disableInternalPushForIncomingCall.
     *
     * @param disableInternalPushForIncomingCall disableInternalPushForIncomingCall.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder disableInternalPushForIncomingCall(final Boolean disableInternalPushForIncomingCall) {
        this.disableInternalPushForIncomingCall = disableInternalPushForIncomingCall;
        return this;
    }

    /*  <DEFAULT_AUDIO_MODE:0>
    \**
     * Sets the audio selection mode.
     *
     * @param audioSelectionMode audio selection mode.
     * @return {@link CallCompositeBuilder} for chaining options.
     *\
    public CallCompositeBuilder audioSelectionMode(final CallCompositeAudioSelectionMode audioSelectionMode) {
        this.audioSelectionMode = audioSelectionMode;
        return this;
    }
    </DEFAULT_AUDIO_MODE:0> */

    /**
     * Sets the communication identifier.
     *
     * @param identifier communication identifier.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder identifier(final CommunicationIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * Builds the CallCompositeClass {@link CallComposite}.
     *
     * @return {@link CallComposite}
     */
    public CallComposite build() {
        final CallCompositeConfiguration config = new CallCompositeConfiguration();
        config.setThemeConfig(themeConfig);
        config.setLocalizationConfig(localizationConfig);
        config.setEnableMultitasking(enableMultitasking);
        config.setEnableSystemPiPWhenMultitasking(enableSystemPiPWhenMultitasking);
        config.setCallScreenOrientation(this.callScreenOrientation);
        config.setSetupScreenOrientation(this.setupScreenOrientation);
        config.setCallScreenOptions(callScreenOptions);
        config.setTelecomManagerOptions(telecomManagerOptions);
        config.setCredential(credential);
        config.setDisplayName(displayName);
        config.setApplicationContext(applicationContext);
        config.setDisableInternalPushForIncomingCall(disableInternalPushForIncomingCall);
        config.setCapabilitiesChangedNotificationMode(capabilitiesChangedNotificationMode);
        config.setSetupScreenOptions(setupScreenOptions);
        /*  <DEFAULT_AUDIO_MODE:0>
        config.setAudioSelectionMode(audioSelectionMode);
        </DEFAULT_AUDIO_MODE:0> */
        config.setIdentifier(identifier);
        return new CallComposite(config);
    }
}
