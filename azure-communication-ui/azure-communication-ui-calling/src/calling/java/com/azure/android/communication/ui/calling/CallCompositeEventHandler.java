// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling;

/**
 * {@link CallCompositeEventHandler}&lt;T&gt;
 *
 * <p>A generic handler for call composite events.</p>
 * <p> - {@link CallComposite#addOnErrorEventHandler(CallCompositeEventHandler)} for Error Handling</p>
 * <p> - {@link CallComposite#removeOnErrorEventHandler(CallCompositeEventHandler)}
 * for Remote Participant Join Notifications</p>
 *
 * @param <T> The callback event Type.
 */
public interface CallCompositeEventHandler<T> {
    /**
     * A callback method to process error event of type T
     *
     * @param eventArgs {@link T}
     */
    void handle(T eventArgs);
}
