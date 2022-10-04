// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import com.azure.android.communication.ui.calling.implementation.CallingIntegrationBridgeImpl;
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeDiagnosticsOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;

import java.util.Collection;

/**
 * Builder for creating {@link CallComposite}.
 *
 * <p>Used to build a {@link CallComposite} which is then used to start a call.</p>
 * <p>This class can be used to specify a Custom theme or locale to be used by the Call Composite</p>
 */
public final class CallCompositeBuilder {

    private Integer themeConfig = null;
    private CallCompositeLocalizationOptions localizationConfig = null;
    private CallingIntegrationBridgeImpl integrationBridge;
    private Collection<CallCompositeCustomButtonViewData> customButtonConfigurations;

    /**
     * Sets an optional theme for call-composite to use by {@link CallComposite}.
     *
     * @param themeId Theme ID.
     * @return {@link CallCompositeBuilder} for chaining options
     */
    public CallCompositeBuilder theme(final int themeId) {
        this.themeConfig = themeId;
        return this;
    }

    /**
     * Sets an optional localization for call-composite to use by {@link CallComposite}.
     *
     * @param localization {@link CallCompositeLocalizationOptions}.
     * @return {@link CallCompositeBuilder} for chaining options
     */
    public CallCompositeBuilder localization(final CallCompositeLocalizationOptions localization) {
        this.localizationConfig = localization;
        return this;
    }

    // TODO: Consider limitation for number of buttons, at least - max 2 for top bar
    // TODO: For CallWithChat library - for top bar max will be 1 addable by Contoso
    public CallCompositeBuilder customButtonViewData(
            final Collection<CallCompositeCustomButtonViewData> customButtonConfigurations) {
        this.customButtonConfigurations = customButtonConfigurations;
        return this;
    }

    public CallCompositeBuilder diagnosticConfiguration(final CallCompositeDiagnosticsOptions diagnosticsOptions) {
        return this;
    }

    CallCompositeBuilder setIntegrationBridge(final CallingIntegrationBridgeImpl integrationBridgeImpl) {
        this.integrationBridge = integrationBridgeImpl;
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
        config.setIntegrationBridge(integrationBridge);
        config.setCustomButtons(customButtonConfigurations);
        return new CallComposite(config);
    }
}
