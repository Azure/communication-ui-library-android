// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

import com.azure.android.communication.ui.configuration.events.CommunicationUIErrorEvent;

/**
 * {@link CallingEventHandler}&lt;T&gt;
 *
 */
public interface CallingEventHandler {
    /**
     * A callback method to process error event
     * @param eventArgs {@link CommunicationUIErrorEvent}
     */
    void handle(CommunicationUIErrorEvent eventArgs);
}
