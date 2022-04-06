// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.CallingEventHandler;
import com.azure.android.communication.ui.persona.PersonaData;

/**
 * LocalParticipantConfiguration for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalParticipantConfiguration with {@link PersonaData}
 * LocalParticipantConfiguration config = new LocalParticipantConfiguration(new PersonaData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., config&#41
 * </pre>
 *
 * @see CallComposite
 */
public class LocalParticipantConfiguration {
    private final PersonaData personaData;

    /**
     * Create LocalParticipantConfiguration.
     *
     * @param personaData The {@link PersonaData};
     * @see PersonaData
     */
    public LocalParticipantConfiguration(final PersonaData personaData) {
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
