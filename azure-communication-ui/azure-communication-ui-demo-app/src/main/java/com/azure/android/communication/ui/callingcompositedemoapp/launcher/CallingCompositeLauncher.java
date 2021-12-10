// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.launcher;

import android.content.Context;

import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public interface CallingCompositeLauncher {
    void launch(Context context,
                String userName,
                UUID groupId,
                String meetingLink,
                Function1<? super String, Unit> showAlert);
}
