// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.persona.PersonaData;

public class LocalParticipantConfiguration {
    private PersonaData personaData;

    public LocalParticipantConfiguration(final PersonaData personaData) {
        this.personaData = personaData;
    }

    @Nullable
    public PersonaData getPersonaData() {
        return personaData;
    }
}
