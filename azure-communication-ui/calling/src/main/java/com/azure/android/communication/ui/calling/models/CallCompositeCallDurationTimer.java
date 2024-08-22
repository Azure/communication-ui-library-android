// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.presentation.manager.CallTimer;

/**
 * Call duration timer for the CallCompositeCallScreenHeaderOptions.
 */
public final class CallCompositeCallDurationTimer {
    CallTimer callTimer;
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
        if (callTimer == null) {
            return;
        }
        callTimer.onStart();
    }

    /**
     * Stop the timer.
     */
    public void stop() {
        if (callTimer == null) {
            return;
        }
        callTimer.onStop();
    }

    /**
     * Reset the timer.
     */
    public void reset() {
        if (callTimer == null) {
            return;
        }
        callTimer.onReset();
    }

    /**
     * Get the elapsed duration of the timer.
     */
    public long getElapsedDuration() {
        if (callTimer == null) {
            if (elapsedDuration == null) {
                return 0;
            }
            return elapsedDuration;
        }
        return callTimer.getElapsedDuration();
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
/* </CUSTOM_CALL_HEADER> */
