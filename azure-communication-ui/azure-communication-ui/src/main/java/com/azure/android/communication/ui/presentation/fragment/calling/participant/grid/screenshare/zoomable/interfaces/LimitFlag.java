// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces;


import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag.LIMIT_ALL;
import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag.LIMIT_NONE;
import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag.LIMIT_SCALE;
import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag.LIMIT_TRANSLATION_X;
import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag.LIMIT_TRANSLATION_Y;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {LIMIT_NONE, LIMIT_TRANSLATION_X, LIMIT_TRANSLATION_Y, LIMIT_SCALE, LIMIT_ALL},
        flag = true)
@Retention(RetentionPolicy.SOURCE)
public @interface LimitFlag {
    int LIMIT_NONE = 0;
    int LIMIT_TRANSLATION_X = 1;
    int LIMIT_TRANSLATION_Y = 1 << 1;
    int LIMIT_SCALE = 1 << 2;
    int LIMIT_ALL = LIMIT_TRANSLATION_X | LIMIT_TRANSLATION_Y | LIMIT_SCALE;
}
