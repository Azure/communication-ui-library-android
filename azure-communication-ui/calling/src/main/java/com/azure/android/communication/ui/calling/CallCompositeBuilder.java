// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import android.content.Context;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions;
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
    private CallCompositeCallScreenOptions callScreenOptions = null;
    private CallCompositeTelecomManagerOptions telecomManagerOptions = null;
    private Context context = null;
    private String displayName = null;
    private CommunicationTokenCredential credential = null;

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
     * @param context application context.
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder context(final Context context) {
        this.context = context;
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
        config.setApplicationContext(context);
        return new CallComposite(config);
    }
}
