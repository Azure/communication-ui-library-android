// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.CallComposite;
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData;

import org.jetbrains.annotations.NotNull;

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
 * &#47;&#47; Build the CommunicationUILocalDataOptions with {@link CommunicationUIPersonaData}
 * CommunicationUILocalDataOptions dataOptions =
 * new CommunicationUILocalDataOptions(new CommunicationUIPersonaData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., dataOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public class CommunicationUILocalDataOptions {
    private final CommunicationUIPersonaData communicationUIPersonaData;

    /**
     * Create CommunicationUILocalDataOptions.
     *
     * @param communicationUIPersonaData The {@link CommunicationUIPersonaData};
     * @see CommunicationUIPersonaData
     */
    public CommunicationUILocalDataOptions(@NotNull final CommunicationUIPersonaData communicationUIPersonaData) {
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
