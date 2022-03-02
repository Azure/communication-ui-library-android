// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant.local;

import com.azure.android.communication.ui.AvatarData;

import org.jetbrains.annotations.NotNull;

/**
 * Get and set local participant configurations
 */
public interface LocalParticipantManager {
    /**
     * @return AvatarData
     */
    @NotNull
    AvatarData getAvatar();

    /**
     * @param avatarData
     */
    void setAvatar(AvatarData avatarData);
}
