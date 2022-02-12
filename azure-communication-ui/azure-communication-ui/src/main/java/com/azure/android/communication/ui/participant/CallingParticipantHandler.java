// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.participant;

import com.azure.android.communication.ui.AvatarPersonaData;

public interface CallingParticipantHandler {
    AvatarPersonaData onFetchRemoteParticipantAvatarData(String userIdentifier);
}
