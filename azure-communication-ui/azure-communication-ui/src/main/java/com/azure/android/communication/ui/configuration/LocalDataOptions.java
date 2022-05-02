// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.persona.PersonaData;

/**
 * LocalDataOptions for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalDataOptions with {@link PersonaData}
 * LocalDataOptions dataOptions =
 * new LocalDataOptions(new PersonaData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., dataOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class LocalDataOptions {
    private final PersonaData personaData;

    /**
     * Create LocalDataOptions.
     *
     * @param personaData The {@link PersonaData};
     * @see PersonaData
     */
    public LocalDataOptions(@NonNull final PersonaData personaData) {
        this.personaData = personaData;
    }


    /**
     * Get current PersonaData
     *
     * @return The {@link PersonaData};
     */
    @Nullable
    public PersonaData getPersonaData() {
        return personaData;
    }
}
