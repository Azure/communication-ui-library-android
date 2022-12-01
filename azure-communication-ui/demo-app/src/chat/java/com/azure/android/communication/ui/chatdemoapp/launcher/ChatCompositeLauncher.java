// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher;

import android.content.Context;

public interface ChatCompositeLauncher {
    void launch(Context context,
                String threadID,
                String endPointURL,
                String displayName,
                String identity);
}
