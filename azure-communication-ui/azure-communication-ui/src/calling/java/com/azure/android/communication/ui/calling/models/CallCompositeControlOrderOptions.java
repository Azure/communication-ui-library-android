// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import androidx.annotation.NonNull;

public final class CallCompositeControlOrderOptions {
    private CallCompositeControlCode firstControl = CallCompositeControlCode.CAMERA_CONTROL;
    private CallCompositeControlCode secondControl = CallCompositeControlCode.MIC_CONTROL;
    private CallCompositeControlCode thirdControl = CallCompositeControlCode.AUDIO_CONTROL;
    private CallCompositeControlCode fourthControl = CallCompositeControlCode.HANGUP_CONTROL;

    public CallCompositeControlOrderOptions() {

    }

    public CallCompositeControlOrderOptions(@NonNull final CallCompositeControlCode firstControl,
                                       @NonNull final CallCompositeControlCode secondControl,
                                       @NonNull final CallCompositeControlCode thirdControl,
                                       @NonNull final CallCompositeControlCode fourthControl) {
        this.firstControl = firstControl;
        this.secondControl = secondControl;
        this.thirdControl = thirdControl;
        this.fourthControl = fourthControl;
    }

    public CallCompositeControlCode getFirstControl() {
        return firstControl;
    }

    public CallCompositeControlCode getSecondControl() {
        return secondControl;
    }

    public CallCompositeControlCode getThirdControl() {
        return thirdControl;
    }

    public CallCompositeControlCode getFourthControl() {
        return fourthControl;
    }
}
