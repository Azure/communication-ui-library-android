// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.presentation.manager.CallTimerAPI;

/**
 * Custom timer for the CallCompositeCallScreenInformationHeader.
 */
public class CallCompositeCallDurationCustomTimer {

    CallTimerAPI callTimerAPI;

    /**
     * Create a CallCompositeCallDurationCustomTimer object.
     */
    public CallCompositeCallDurationCustomTimer() {
    }

    /**
     * Start the timer.
     */
    public void start() {
        if (callTimerAPI == null) {
            return;
        }
        callTimerAPI.onStart();
    }

    /**
     * Stop the timer.
     */
    public void stop() {
        if (callTimerAPI == null) {
            return;
        }
        callTimerAPI.onStop();
    }

    /**
     * Reset the timer.
     */
    public void reset() {
        if (callTimerAPI == null) {
            return;
        }
        callTimerAPI.onReset();
    }
}
