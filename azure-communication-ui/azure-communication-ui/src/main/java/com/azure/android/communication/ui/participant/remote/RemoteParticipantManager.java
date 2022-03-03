// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.remote;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.PersonaData;

import org.jetbrains.annotations.NotNull;

/**
 * Get and set remote participant configurations
 */
public interface RemoteParticipantManager {
    /**
     * @param communicationIdentifier
     * @param personaData
     */
    void setPersonaData(CommunicationIdentifier communicationIdentifier, PersonaData personaData);

    /**
     * @param communicationIdentifier
     * @return AvatarData
     */
    @NotNull
    PersonaData getAvatar(CommunicationIdentifier communicationIdentifier);
}
