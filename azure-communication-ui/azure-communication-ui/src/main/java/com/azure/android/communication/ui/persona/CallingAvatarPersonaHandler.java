// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona;

import com.azure.android.communication.ui.AvatarPersonaData;

public interface CallingAvatarPersonaHandler {
    AvatarPersonaData onFetchAvatarPersonaData(String userIdentifier);
}
