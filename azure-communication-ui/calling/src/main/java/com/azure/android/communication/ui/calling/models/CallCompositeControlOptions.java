// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.Collection;

/**
 * UI Control Options.
 */
public final class CallCompositeControlOptions {
    private Collection<CallCompositeCustomButtonViewData> customButtonConfigurations;

    /**
     * Constructs {@link CallCompositeControlOptions}.
     * @param customButtonConfigurations {@link CallCompositeCustomButtonViewData}.
     */
    public CallCompositeControlOptions(
            final Collection<CallCompositeCustomButtonViewData> customButtonConfigurations) {
        this.customButtonConfigurations = customButtonConfigurations;
    }

    /**
     * Get collection of custom buttons.
     * @return {@link CallCompositeCustomButtonViewData}.
     */
    public Collection<CallCompositeCustomButtonViewData> getCustomButtonViewData() {
        return customButtonConfigurations;
    }
}
