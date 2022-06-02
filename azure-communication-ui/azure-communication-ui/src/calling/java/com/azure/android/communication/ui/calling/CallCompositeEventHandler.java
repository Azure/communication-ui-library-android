// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

/**
 * {@link CallCompositeEventHandler}&lt;T&gt;
 */
public interface CallCompositeEventHandler<T> {
    /**
     * A callback method to process error event
     *
     * @param eventArgs {@link T}
     */
    void handle(T eventArgs);
}
