// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.presentation.manager.CallTimerAPI;

/**
 * Call duration timer for the CallCompositeCallScreenInformationHeader.
 */
public class CallCompositeCallDurationTimer {

    CallTimerAPI callTimerAPI;
    Long startDuration;

    /**
     * Create a CallCompositeCallDurationTimer object.
     */
    public CallCompositeCallDurationTimer() {
    }

    /**
     * Start the timer.
     * By default, the timer will start with a duration of previous stop.
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

    /**
     * Get the start duration of the timer.
     */
    public long getStartDuration() {
        return startDuration;
    }

    /**
     * Set the start duration of the timer in seconds.
     */
    public void setStartDuration(final long startDuration) {
        this.startDuration = startDuration;
    }
}
