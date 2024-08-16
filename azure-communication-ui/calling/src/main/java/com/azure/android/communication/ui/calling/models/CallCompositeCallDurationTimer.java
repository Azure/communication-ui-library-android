// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.presentation.manager.CallTimerAPI;

/**
 * Call duration timer for the CallCompositeCallScreenHeaderOptions.
 */
public class CallCompositeCallDurationTimer {

    CallTimerAPI callTimerAPI;
    Long elapsedDuration;

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
     * Get the elapsed duration of the timer.
     */
    public long getElapsedDuration() {
        if (callTimerAPI == null) {
            return elapsedDuration;
        }
        return callTimerAPI.getElapsedDuration();
    }

    /**
     * Set the elapsed duration of the timer.
     *
     * @param elapsedDuration The elapsed duration.
     * @return The {@link CallCompositeCallDurationTimer} object itself.
     */
    public CallCompositeCallDurationTimer setElapsedDuration(final long elapsedDuration) {
        this.elapsedDuration = elapsedDuration;
        return this;
    }
}
