// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

/**
 * Event with error type and caused throwable.
 */
public final class ChatCompositeEvent {
    private final ChatCompositeEventCode code;

    /**
     * Create {@link ChatCompositeEvent} with {@link ChatCompositeEventCode}.
     *
     * @param code  Error code {@link ChatCompositeEventCode}.
     */
    public ChatCompositeEvent(final ChatCompositeEventCode code) {
        this.code = code;
    }


    /**
     * Returns the event source.
     *
     * @return the event code {@link ChatCompositeEventCode} instance.
     */
    public ChatCompositeEventCode getEventCode() {
        return code;
    }
}
