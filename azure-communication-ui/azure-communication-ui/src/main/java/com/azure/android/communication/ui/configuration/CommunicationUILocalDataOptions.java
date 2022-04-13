// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData;

/**
 * CommunicationUILocalDataOptions for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalParticipantConfiguration with {@link CommunicationUIPersonaData}
 * LocalParticipantConfiguration config = new LocalParticipantConfiguration(new PersonaData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., config&#41
 * </pre>
 *
 * @see CallComposite
 */
public class CommunicationUILocalDataOptions {
    private final CommunicationUIPersonaData communicationUIPersonaData;

    /**
     * Create LocalParticipantConfiguration.
     *
     * @param communicationUIPersonaData The {@link CommunicationUIPersonaData};
     * @see CommunicationUIPersonaData
     */
    public CommunicationUILocalDataOptions(final CommunicationUIPersonaData communicationUIPersonaData) {
        this.communicationUIPersonaData = communicationUIPersonaData;
    }


    /**
     * Get current PersonaData
     *
     * @return The {@link CommunicationUIPersonaData};
     */
    @Nullable
    public CommunicationUIPersonaData getPersonaData() {
        return communicationUIPersonaData;
    }
}
