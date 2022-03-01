// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.remote;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.AvatarPersonaData;

import org.jetbrains.annotations.NotNull;

public interface RemoteParticipantManager {
    void setRemoteParticipantAvatar(CommunicationIdentifier communicationIdentifier, AvatarPersonaData avatarPersonaData);

    @NotNull
    AvatarPersonaData getRemoteParticipantAvatar(CommunicationIdentifier communicationIdentifier);

}
