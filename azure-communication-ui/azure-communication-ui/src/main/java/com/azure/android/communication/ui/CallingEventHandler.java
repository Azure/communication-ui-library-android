// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

/**
 * {@link CallingEventHandler}&lt;T&gt;
 *
 */
public interface CallingEventHandler<T> {
    /**
     * A callback method to process event
     * @param eventArgs {@link T}
     */
    void handle(T eventArgs);
}
