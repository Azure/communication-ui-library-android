// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;

/**
 * Builder for creating {@link CallComposite}.
 */
public final class CallCompositeBuilder {

    private Integer themeConfig = null;
    private CallCompositeLocalizationOptions localizationConfig = null;

    /**
     * Sets an optional theme for call-composite to use by {@link CallComposite}.
     *
     * @param themeId Theme ID.
     * @return {@link CallCompositeBuilder}
     */
    public CallCompositeBuilder theme(final int themeId) {
        this.themeConfig = themeId;
        return this;
    }

    /**
     * Sets an optional localization for call-composite to use by {@link CallComposite}.
     *
     * @param localization {@link CallCompositeLocalizationOptions}.
     * @return {@link CallCompositeBuilder}
     */
    public CallCompositeBuilder localization(final CallCompositeLocalizationOptions localization) {
        this.localizationConfig = localization;
        return this;
    }

    /**
     * Creates {@link CallComposite}.
     *
     * @return {@link CallComposite}
     */
    public CallComposite build() {
        final CallCompositeConfiguration config = new CallCompositeConfiguration();
        config.setThemeConfig(themeConfig);
        config.setLocalizationConfig(localizationConfig);

        return new CallComposite(config);
    }
}
