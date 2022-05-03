// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

import com.azure.android.communication.ui.calling.configuration.LocalizationConfiguration;
import com.azure.android.communication.ui.calling.configuration.ThemeConfiguration;
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration;

/**
 * Builder for creating {@link CallComposite}.
 */
public final class CallCompositeBuilder {

    private ThemeConfiguration themeConfig = null;
    private LocalizationConfiguration localizationConfig = null;

    /**
     * Sets an optional theme for call-composite to use by {@link CallComposite}.
     *
     * @param theme {@link ThemeConfiguration}.
     * @return {@link CallCompositeBuilder}
     */
    public CallCompositeBuilder theme(final ThemeConfiguration theme) {
        this.themeConfig = theme;
        return this;
    }

    /**
     * Sets an optional localization for call-composite to use by {@link CallComposite}.
     *
     * @param localization {@link LocalizationConfiguration}.
     * @return {@link CallCompositeBuilder}
     */
    public CallCompositeBuilder localization(final LocalizationConfiguration localization) {
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
