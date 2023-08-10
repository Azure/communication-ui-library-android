// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callwithchatdemoapp.launcher;

import android.content.Context;

import com.azure.android.communication.ui.callwithchatdemoapp.AlertHandler;
import com.azure.android.communication.ui.demoapp.AuthService;

import java.util.UUID;


public interface CallWithChatCompositeLauncher {
    void launch(Context context,
                AlertHandler alertHandler,
                AuthService authService,
                String userName,
                String acsEndpoint,
                UUID groupId,
                String chatThreadId,
                String meetingLink);
}
