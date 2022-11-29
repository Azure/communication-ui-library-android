// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.chat

/**
 * [ChatCompositeEventHandler]&lt;T&gt;
 *
 *
 * A generic handler for chat composite events.
 *
 * @param <T> The callback event Type.
</T> */
internal interface ChatCompositeEventHandler<T> {
    /**
     * A callback method to process error event of type T
     *
     * @param eventArgs [T]
     */
    fun handle(eventArgs: T)
}
