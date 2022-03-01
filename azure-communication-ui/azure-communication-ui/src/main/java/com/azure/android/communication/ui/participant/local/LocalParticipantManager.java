// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.local;

import com.azure.android.communication.ui.AvatarPersonaData;

import org.jetbrains.annotations.NotNull;

public interface LocalParticipantManager {
    void setLocalParticipantAvatar(AvatarPersonaData avatarPersonaData);

    @NotNull
    AvatarPersonaData getLocalParticipantAvatar();
}
