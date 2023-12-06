// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation;
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions;

/**
 * Builder for creating {@link CallComposite}.
 *
 * <p>Used to build a {@link CallComposite} which is then used to start a call.</p>
 * <p>This class can be used to specify a Custom theme or locale to be used by the Call Composite.</p>
 */
public final class CallCompositeBuilder {

    private Integer themeConfig = null;
    private CallCompositeLocalizationOptions localizationConfig = null;
    private CallCompositeSupportedScreenOrientation callScreenOrientation = null;
    private CallCompositeSupportedScreenOrientation setupScreenOrientation = null;
    private CallCompositeTelecomOptions telecomOptions = null;
    private Boolean enableMultitasking = false;
    private Boolean enableSystemPiPWhenMultitasking = false;

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

    /***
     * Sets an optional telecom options for call-composite
     *
     * @param telecomOptions {@link CallCompositeTelecomOptions}
     * @return {@link CallCompositeBuilder} for chaining options.
     */
    public CallCompositeBuilder telecom(
            final CallCompositeTelecomOptions telecomOptions) {
        this.telecomOptions = telecomOptions;
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
        this.enableMultitasking = options.isEnableMultitasking();
        this.enableSystemPiPWhenMultitasking = options.isEnableSystemPictureInPictureWhenMultitasking();
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
        config.setCallScreenOrientation(this.callScreenOrientation);
        config.setSetupScreenOrientation(this.setupScreenOrientation);
        config.setTelecomOptions(this.telecomOptions);
        config.setEnableMultitasking(enableMultitasking);
        config.setEnableSystemPiPWhenMultitasking(enableSystemPiPWhenMultitasking);
        return new CallComposite(config);
    }
}
