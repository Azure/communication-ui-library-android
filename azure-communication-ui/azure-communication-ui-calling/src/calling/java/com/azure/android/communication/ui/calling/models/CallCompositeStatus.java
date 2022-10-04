// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;


public final class CallCompositeStatus {

    public CallCompositeCallStatus getCallStatus() {
        return CallCompositeCallStatus.CONNECTED;
    }

    public CallCompositeNavigationStatus getNavigationStatus() {
        return CallCompositeNavigationStatus.CALL;
    }
}
