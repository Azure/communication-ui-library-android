// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona

internal class CallCompositeAvatarPersonaHandler {
    private var avatarPersonaHandler: CallingAvatarPersonaHandler? = null

    fun getOnAvatarPersonaHandler() = avatarPersonaHandler

    fun setOnAvatarPersonaHandler(avatarPersonaHandler: CallingAvatarPersonaHandler?) {
        this.avatarPersonaHandler = avatarPersonaHandler
    }
}
