// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * LocalSettings for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalSettings with {@link PersonaData}
 * LocalSettings settings =
 * new LocalSettings(new PersonaData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., dataOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class LocalSettings {
    private final PersonaData personaData;

    /**
     * Create LocalSettings.
     *
     * @param personaData The {@link PersonaData};
     * @see PersonaData
     */
    public LocalSettings(final PersonaData personaData) {
        this.personaData = personaData;
    }


    /**
     * Get current PersonaData
     *
     * @return The {@link PersonaData};
     */
    public PersonaData getPersonaData() {
        return personaData;
    }
}
