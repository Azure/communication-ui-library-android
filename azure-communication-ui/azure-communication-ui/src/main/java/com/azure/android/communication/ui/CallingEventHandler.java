// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

/**
 * {@link CallingEventHandler}&lt;T&gt;
 * @param <T> an event handler eventArgs type
 */
public interface CallingEventHandler<T> {
    void handle(T eventArgs);
}
