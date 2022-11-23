// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

/**
 * {@link ChatCompositeEventHandler}&lt;T&gt;
 *
 * <p>A generic handler for chat composite events.</p>
 *
 * @param <T> The callback event Type.
 */
public interface ChatCompositeEventHandler<T> {
    /**
     * A callback method to process error event of type T
     *
     * @param eventArgs {@link T}
     */
    void handle(T eventArgs);
}
