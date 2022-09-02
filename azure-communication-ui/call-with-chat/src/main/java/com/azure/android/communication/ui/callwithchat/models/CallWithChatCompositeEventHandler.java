// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import com.azure.android.communication.ui.callwithchat.CallWithChatComposite;

/**
 * {@link CallWithChatCompositeEventHandler}&lt;T&gt;
 *
 * <p>A generic handler for call composite events.</p>
 * <p> - {@link CallWithChatComposite#addOnErrorEventHandler(CallWithChatCompositeEventHandler)} for Error Handling</p>
 * <p> - {@link CallWithChatComposite#removeOnErrorEventHandler(CallWithChatCompositeEventHandler)}
 * for Remote Participant Join Notifications</p>
 *
 * @param <T> The callback event Type.
 */
public interface CallWithChatCompositeEventHandler<T> {
    /**
     * A callback method to process error event of type T
     *
     * @param eventArgs {@link T}
     */
    void handle(T eventArgs);
}
