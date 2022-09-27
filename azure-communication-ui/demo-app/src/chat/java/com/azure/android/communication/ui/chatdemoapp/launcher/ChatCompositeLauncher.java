// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher;

import com.azure.android.communication.ui.chatdemoapp.ChatLauncherActivity;

public interface ChatCompositeLauncher {
    void launch(ChatLauncherActivity chatLauncherActivity,
                String threadID,
                String endPointURL, String toString);
}
