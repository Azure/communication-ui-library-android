// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callwithchatdemoapp.launcher;

import com.azure.android.communication.ui.callwithchatdemoapp.CallWithChatLauncherActivity;

import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public interface CallWithChatCompositeLauncher {
    void launch(CallWithChatLauncherActivity callLauncherActivity,
                String userName,
                UUID groupId,
                String meetingLink,
                Function1<? super String, Unit> showAlert);
}
